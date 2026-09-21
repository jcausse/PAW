package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.model.File;
import ar.edu.itba.paw.model.Listing;
import ar.edu.itba.paw.model.Offer;
import ar.edu.itba.paw.model.OfferStatus;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.service.OfferService;
import ar.edu.itba.paw.service.exception.BadParameterException;
import ar.edu.itba.paw.service.exception.ForbiddenException;
import ar.edu.itba.paw.service.exception.NotFoundException;
import ar.edu.itba.paw.webapp.auth.CurrentUser;
import ar.edu.itba.paw.webapp.form.ProofOfPaymentUploadForm;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

@RequiredArgsConstructor
@Controller
@RequestMapping("/offer")
public class OfferController {

    private final OfferService offerService;

    @GetMapping("/{offerId}")
    public ModelAndView viewOffer(@PathVariable Long offerId, @CurrentUser User currentUser) {
        final Offer offer = offerService.getById(offerId)
            .orElseThrow(() -> NotFoundException.createFor("Offer"));

        final Listing listing = offer.getListing();
        final Long currentUserId = currentUser.getId();

        // Verify the current user is the seller (listing creator)
        if (!listing.getCreator().getId().equals(currentUserId)) {
            throw new ForbiddenException("Not authorized to view this offer");
        }

        var mav = new ModelAndView("offer/detail");
        mav.addObject("offer", offer);
        return mav;
    }

    @PostMapping("/{offerId}/accept")
    public ModelAndView acceptOffer(@PathVariable Long offerId, @CurrentUser User currentUser) {
        offerService.accept(offerId, currentUser.getId());

        return new ModelAndView("redirect:/account/incoming-offers?statusGroup=pending_payment");
    }

    @PostMapping("/{offerId}/reject")
    public ModelAndView rejectOffer(@PathVariable Long offerId, @CurrentUser User currentUser) {
        offerService.reject(offerId, currentUser.getId());

        return new ModelAndView("redirect:/account/incoming-offers?statusGroup=resolved");
    }

    @PostMapping("/{offerId}/withdraw")
    public ModelAndView withdrawOffer(@PathVariable Long offerId,
                                       @CurrentUser User currentUser,
                                       @RequestHeader(value = "Referer", required = false) String referer) {
        final Offer offer = offerService.getById(offerId)
            .orElseThrow(() -> NotFoundException.createFor("Offer"));

        final Listing listing = offer.getListing();
        offerService.withdraw(offerId, currentUser.getId());

        String redirectUrl = "/listing/" + listing.getId();
        if (referer != null && (referer.contains("/account/my-offers") || referer.contains("/account/incoming-offers"))) {
            redirectUrl = referer;
        }
        return new ModelAndView("redirect:" + redirectUrl);
    }

    @PostMapping("/{offerId}/confirm-payment")
    public ModelAndView confirmPayment(@PathVariable Long offerId, @CurrentUser User currentUser) {
        offerService.confirmPayment(offerId, currentUser.getId());

        return new ModelAndView("redirect:/account/incoming-offers?statusGroup=resolved");
    }

    @GetMapping("/{offerId}/proof-of-payment")
    public ModelAndView showProofOfPaymentUpload(@PathVariable Long offerId, @CurrentUser User currentUser) {
        final Offer offer = offerService.getById(offerId)
            .orElseThrow(() -> NotFoundException.createFor("Offer"));

        if (!offer.getBuyer().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("Not authorized to upload proof of payment");
        }

        if (offer.getStatus() != OfferStatus.PENDING_PAYMENT) {
            throw new BadParameterException("Offer is not pending payment");
        }

        var mav = new ModelAndView("offer/proofOfPaymentUpload");
        mav.addObject("offer", offer);
        mav.addObject("proofOfPaymentUploadForm", new ProofOfPaymentUploadForm());
        return mav;
    }

    @PostMapping("/{offerId}/proof-of-payment")
    public ModelAndView uploadProofOfPayment(@PathVariable Long offerId,
                                              @CurrentUser User currentUser,
                                              @Valid @ModelAttribute("proofOfPaymentUploadForm") ProofOfPaymentUploadForm form,
                                              BindingResult bindingResult) {
        final Offer offer = offerService.getById(offerId)
            .orElseThrow(() -> NotFoundException.createFor("Offer"));

        if (bindingResult.hasErrors()) {
            var mav = new ModelAndView("offer/proofOfPaymentUpload");
            mav.addObject("offer", offer);
            mav.addObject("currentUser", currentUser);
            return mav;
        }

        try {
            offerService.uploadProofOfPayment(
                offerId,
                currentUser.getId(),
                form.getFile().getOriginalFilename(),
                "Proof of payment for offer " + offerId,
                form.getFile().getContentType(),
                form.getFile().getBytes()
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to read uploaded file", e);
        }

        return new ModelAndView("redirect:/account/my-offers?statusGroup=pending_payment");
    }

    @GetMapping("/{offerId}/proof-of-payment/download")
    public ResponseEntity<Resource> downloadProofOfPayment(@PathVariable Long offerId, @CurrentUser User currentUser) {
        final Offer offer = offerService.getById(offerId)
            .orElseThrow(() -> NotFoundException.createFor("Offer"));

        // Both buyer and seller can download the proof of payment
        final Long currentUserId = currentUser.getId();
        final boolean isBuyer = offer.getBuyer().getId().equals(currentUserId);
        final boolean isSeller = offer.getListing().getCreator().getId().equals(currentUserId);

        if (!isBuyer && !isSeller) {
            throw new ForbiddenException("Not authorized to download proof of payment");
        }

        final File file = offerService.getProofOfPaymentFile(offerId)
            .orElseThrow(() -> NotFoundException.createFor("Proof of payment not found"));

        final ByteArrayResource resource = new ByteArrayResource(file.getData());

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(file.getContentType().orElse("application/octet-stream")))
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
            .contentLength(file.getData().length)
            .body(resource);
    }
}
