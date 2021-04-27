package it.arrp.c3.Service;

import it.arrp.c3.Model.Box;
import it.arrp.c3.Model.Corriere;
import it.arrp.c3.Model.Corsa;
import it.arrp.c3.Model.Enum.StatoCorriere;
import it.arrp.c3.Model.Repository.CorriereRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Classe che si occupa di effettuare le operazioni riguardanti la classe Corriere.
 */
@Service
public class ServiceCorriere {

    @Autowired
    CorriereRepository repoCorriere;
    @Autowired
    ServiceCorsa serviceCorsa;
    @Autowired
    ServiceBox serviceBox;

    public List<Corsa> getAllCorse(Long idCorriere){ //Rinominato per essere piu' specifico
        return repoCorriere.findOneById(idCorriere).getAllCorse();
    }

    public Corsa getCorsa(Long idCorriere, Long idCorsa){
        return repoCorriere.findOneById(idCorriere).getCorsa(idCorsa);
    }

    //forse il return dovrebbe essere void, ma fa comodo per verificare subito l'effetto dell'operazione
    public List<Corsa> rifiutaCorsa(Long idCorriere, Long idCorsa){
        Corriere corr = repoCorriere.findOneById(idCorriere);
        if(corr == null)
            return null;
        if(corr.getCorsa(idCorsa) == null) {
            return null;
        }else {
            serviceCorsa.rifiutaCorsa(idCorriere, idCorsa);
            corr.rimuoviCorsa(getCorsa(idCorriere, idCorsa));
        }
        return corr.getAllCorse();
    }
    public Corriere cambiaStato(Long idCorriere, StatoCorriere statoCorriere){
        Corriere c = repoCorriere.findOneById(idCorriere);
        c.setStato(statoCorriere);
        return c;
    }

    //input controllato da service Negozio. La notifica la manda serviceCorsa una volta verificato il tutto.
    public void assegnaCorsa(Corsa corsa, Long idCorriere) {
        Corriere corriere = getCorriere(idCorriere);
        corriere.addNuovaCorsa(corsa);
    }

    public Corriere getCorriere(Long uuidCorriere){
        return repoCorriere.findOneById(uuidCorriere);
    }

    /**
     * Sblocca il Box per inserire il pacco da consegnare e attraverso questo "innesca" il completamento
     * della corsa assegnata.
     * @param idCorriere identifica il corriere che deve aprire il box.
     * @param idCorsa identifica la corsa per vedere le info e completarla.
     */
    public Box unlock(Long idCorriere, Long idCorsa) {
        if(getCorsa(idCorriere,idCorsa) != null){
            //sblocco il box
            Corsa c = serviceCorsa.getCorsa(idCorsa);
            serviceBox.unlock(c.getIdBox());
            //completo la corsa
            serviceCorsa.corsaCompletata(idCorsa);
            completaCorsa(idCorriere,c);
            return serviceBox.getBox(c.getIdBox());
        }
        return null;
    }

    /**
     * Per completare una corsa, notifica il serviceCorsa e intanto elimina le risorse che non sono piu' utili.
     */
    private void completaCorsa(Long idCorriere, Corsa corsa) {
        serviceCorsa.corsaCompletata(corsa.getIdCorsa());
        Corriere c = getCorriere(idCorriere);
        c.rimuoviCorsa(corsa);
    }

    public void salvaRuoloCorriere(Long idCliente, String mdt) {
        repoCorriere.save(new Corriere(idCliente,mdt));
    }

}
