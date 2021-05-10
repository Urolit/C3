package it.arrp.c3.Service;

import it.arrp.c3.Model.Admin;
import it.arrp.c3.Model.Box;
import it.arrp.c3.Model.Locker;
import it.arrp.c3.Model.Repository.TecnicoRepository;
import it.arrp.c3.Model.Tecnico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Classe che si occupa di effettuare le operazioni riguardanti la classe Tecnico.
 */
@Service
public class ServiceTecnico {

    @Autowired
    ServiceCliente serviceCliente;
    @Autowired
    ServiceLocker serviceLocker;
    @Autowired
    ServiceMessaggio serviceMessaggio;
    @Autowired
    ServiceBox serviceBox;
    @Autowired
    ServiceAdmin serviceAdmin;
    @Autowired
    TecnicoRepository repoTecnico;


    public boolean creaTecnico(Long idCliente, Admin admin) {
        if(idCliente == null || admin == null)
            return false;
        serviceCliente.aggiungiRuoloTecnico(idCliente);
        repoTecnico.save(new Tecnico(idCliente,admin));
        return true;
    }

    public Tecnico getTecnico(Long idTecnico){
        return repoTecnico.findOneById(idTecnico);
    }

    public boolean creaRichiesta( Long idTecnico, String testoRichiesta){
        return serviceMessaggio.sendRichiesta(idTecnico,repoTecnico.findOneById(idTecnico).
                getAdmin().getIdCLiente(),testoRichiesta);
    }

    /**
     * Questo metodo controlla che il tecnico lavori nella stessa citta dove risiede il locker.
     * Cosi' facendo un tecnico non potra' effettuare operazioni "globali".
     */
    private boolean controlloInputLocker(Long idTecnico, Long idLocker){
        Tecnico tec = getTecnico(idTecnico);
        Locker locker = serviceLocker.getLockerById(idLocker);
        if(tec == null || locker == null)
            return false;
        return tec.getCittaDiLavoro().equals(locker.getCitta());
    }

    //Nota: Il tipo di ritorno e' locker cosi' dal controller possiamo vedere subito il cambiamento
    // senza dover fare anche il getLockerAttivo
    public Locker impostaLockerAttivo(Long idTecnico, Long idLocker){
        if(!controlloInputLocker(idTecnico, idLocker))
            return null;
        Tecnico tec = getTecnico(idTecnico);
        tec.setLockerAttivo(serviceLocker.getLockerById(idLocker));
        return tec.getLockerAttivo();
    }

    public boolean rimuoviLockerAttivo(Long idTecnico){
        Tecnico tec = getTecnico(idTecnico);
        if(tec != null) {
            tec.setLockerAttivo(null);
            return true;
        }else
            return false;
    }

    /**
     * Questo metodo controlla che il tecnico sia collegato al locker; viene utilizzato prima di eseguire i comandi
     * cosi' siamo sicuri che non esegua comandi su oggetti che non gli appartengono dato che per collegare un locker
     * avvengono delle verifiche.
     */
    private boolean controllaLockerCollegato(Long idTecnico, Long idLocker){
        if(!controlloInputLocker(idTecnico, idLocker))
            return false;
        return getTecnico(idTecnico).getLockerAttivo().equals(serviceLocker.getLockerById(idLocker));
    }

    public Locker turnOnLocker(Long idTecnico, Long idLocker){
        if(controllaLockerCollegato(idTecnico, idLocker))
            serviceLocker.turnOnLocker(idLocker);
        return serviceLocker.getLockerById(idLocker);
    }

    public Locker turnOffLocker(Long idTecnico, Long idLocker){
        if(controllaLockerCollegato(idTecnico, idLocker))
            serviceLocker.turnOffLocker(idLocker);
        return serviceLocker.getLockerById(idLocker);
    }

    /*
    NOTA: I metodi che gestiscono i box fanno un sacco di giri in piu', per comodita' e per completare in tempo
    il progetto credo rimarranno cosi' dato che sono corretti, ma potrebbero essere ottimizzati in un secondo momento.
     */

    public Box turnOnBox(Long idTecnico, Long idBox){
        Box box = serviceBox.getBox(idBox);
        if(box == null)
            return null;
        if(controllaLockerCollegato(idTecnico, box.getLocker().getId()))
            serviceBox.turnOnBox(idBox);
        return box;
    }

    public Box turnOffBox(Long idTecnico, Long idBox){
        Box box = serviceBox.getBox(idBox);
        if(box == null)
            return null;
        if(controllaLockerCollegato(idTecnico, box.getLocker().getId()))
            serviceBox.turnOffBox(idBox);
        return box;
    }

    public Box unlockBox(Long idTecnico, Long idBox){
        Box box = serviceBox.getBox(idBox);
        if(box == null)
            return null;
        if(controllaLockerCollegato(idTecnico, box.getLocker().getId()))
            serviceBox.unlock(idBox);
        return box;
    }

    public Box lockBox(Long idTecnico, Long idBox){
        Box box = serviceBox.getBox(idBox);
        if(box == null)
            return null;
        if(controllaLockerCollegato(idTecnico, box.getLocker().getId()))
            serviceBox.lock(idBox);
        return box;
    }

}