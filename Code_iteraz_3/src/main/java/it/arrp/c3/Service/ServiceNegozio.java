package it.arrp.c3.Service;

import it.arrp.c3.Model.Cliente;
import it.arrp.c3.Model.Corriere;
import it.arrp.c3.Model.Enum.GenereNegozio;
import it.arrp.c3.Model.Enum.StatoCorriere;
import it.arrp.c3.Model.Negozio;
import it.arrp.c3.Model.Repository.NegozioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe che si occupa di effettuare le operazioni riguardanti il Ruolo Negozio.
 * Quando ci riferiamo al cliente viene chiamato IDCommerciante, mentre quando ci riferiamo
 * al negozio come oggetto fisico usiamo IDNegozio.
 */
@Service
public class ServiceNegozio {

    @Autowired
    NegozioRepository repoNegozio;
    @Autowired
    ServiceNegozio serviceNegozio;
    @Autowired
    ServiceCorriere serviceCorriere;
    @Autowired
    ServiceCliente serviceCliente;
    @Autowired
    ServiceCorsa serviceCorsa;
    @Autowired
    ServiceLocker serviceLocker;

    public boolean addCorriere(Long idNegozio, Long idCorriere){
        if (controllaInput(idNegozio,idCorriere))
            return false;
        return serviceNegozio.addCorriere(idNegozio, idCorriere);
    }

    public boolean removeCorriere(Long idNegozio, Long idCorriere){
        if (controllaInput(idNegozio,idCorriere))
            return false;
        return serviceNegozio.removeCorriere(idNegozio, idCorriere);
    }

    private boolean controllaInput(Long idNegozio, Long idCorriere) {
        Negozio neg = repoNegozio.findOneById(idNegozio);
        Corriere corr = serviceCorriere.getCorriere(idCorriere);
        return neg == null || corr == null;
    }

    public Long getCorriereDisponibile(Long idCommerciante){
        Negozio negozio = getNegozioById(idCommerciante);
        if (negozio == null)
            return null;
        return getListaCorrieriDisponibili(negozio).get(0).getIdCLiente();
    }

    public List<Corriere> getListaCorrieriDisponibili(Negozio negozio){
        ArrayList<Corriere> lista = negozio.getListaCorrieriAssunti();
        lista.removeIf(x-> !(x.getStato() == StatoCorriere.Attivo));
        return lista;
    }

    /**
     * Questo metodo chiede di generare una corsa al relativo service.
     * Returns 1 se non ci sono stati problemi
     * Returns 0 se il cliente non ha box assegnati
     * Returns -1 se non vi e' nessun corriere disponibile
     * Returns -2 se uno dei codici identificativi é errato
     *
     * Nota: Gli input e oggetti utilizzati vengono controllati qui e in caso positivo service corsa si
     * occupa di avviare il tutto, senza ricontrollare i dati.
     *
     * TODO nel VPP il serviceCliente controlla il checkpoint e in caso positivo ritorna un box assegnato
     * TODO quindi bisogna modificare il VPP!? --Ric
     *  si, ci pensa serviceLocker a controllare il suo checkpoint e procedere con l'assegnamento, quindi
     *  va aggiornato il VPP
     */
    public int creaCorsa(Long idCliente, Long idNegozio){
        if(controllaInputCorsa(idCliente, idNegozio))
            return -2; //errore id non valido
        Long idCorriere = getCorriereDisponibile(idNegozio);
        if (idCorriere!=null){
            Long idBox = serviceLocker.assegnaBox(idCliente);
            if(idBox != null){
                serviceCorsa.creaCorsa(idNegozio, idCorriere, idCliente, idBox);
                return 1;
            }else return 0;
        }else return -1; //di conseguenza la consegna viene negata
    }

    private boolean controllaInputCorsa(Long idCliente, Long idCommerciante) {
        Cliente cliente = serviceCliente.getCliente(idCliente);
        Negozio commerciante = repoNegozio.findOneById(idCommerciante);
        return cliente == null || commerciante == null;
    }

    private Negozio getNegozioById(Long idNegozio){
        return this.repoNegozio.findOneById(idNegozio);
    }

    public List<Negozio> getNegozi(String citta) {
        List<Negozio> lista = this.repoNegozio.findAll();
        lista.removeIf(n -> !(n.getCittaNegozio().equals(citta)));
        return lista;
        //TODO Ripensandoci sarebbe piuttosto semplice con una richiesta SQL:
        // select * from Negozi where citta = "variabile"
        // non credo si possa fare manualmente, forse conviene lasciarlo cosi' per non complicarsi troppo,
        // potremmo vederlo successivamente
    }

    public List<Negozio> getNegozioByName(String citta, String nome) {
        List<Negozio> lista = getNegozi(citta);
        lista.removeIf(x -> !(x.getNomeNegozio().equals(nome)));
        return lista;
    }

    public List<Negozio> getNegozioByGenere(String citta, GenereNegozio genere) {
        List<Negozio> lista = getNegozi(citta);
        lista.removeIf(x -> !(x.getGenereNegozio() == genere));
        return lista;
    }

    public List<Corriere> getCorrieri(Long idCommerciante) {
        Negozio n = getNegozioById(idCommerciante);
        if(n == null)
            return null;
        return n.getListaCorrieriAssunti();
    }

    public void salvaRuoloNegozio(Long idCliente, String nomeNegozio, String cittaNegozio, GenereNegozio genere) {
        repoNegozio.save(new Negozio(idCliente,nomeNegozio,cittaNegozio,genere));
    }

    public Negozio getNegozio(Long id){ return repoNegozio.findOneById(id); }

}
