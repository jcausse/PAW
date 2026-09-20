package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.ProofOfPayment;
import java.util.Optional;

public interface ProofOfPaymentDao {
    Optional<ProofOfPayment> getById(Long id);
    
    ProofOfPayment create(String filename, String alt, String contentType, byte[] data);
    
    void delete(Long id);
}