package it.arrp.c3.Model;

import it.arrp.c3.Model.Enum.Accensione;
import it.arrp.c3.Model.Enum.Chiusura;
import it.arrp.c3.Model.Enum.StatoBox;

import javax.persistence.*;
import java.util.Objects;

/**
 * Questa classe ha la responsabilità di gestire un singolo Box facente parte di
 * un Locker. Ne consegue che gestirà il proprio stato, la chiave di apertura
 * e, per questa implementazione, anche la sua apertura/chiusura simulata.
 */
@Entity
@Table(name = "box")
public class Box{

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = "idBox")
    private Long idBox;
    @Column(name = "clienteAssegnato")
    private Long idCliente;
    @Column(name = "Locker")
    private Long locker;
    @Enumerated(EnumType.STRING)
    private Chiusura chiusura;
    @Enumerated(EnumType.STRING)
    private StatoBox stato;
    @Enumerated(EnumType.STRING)
    private Accensione statoAccensioneBox;

    public Box() {
    }

    public Box(Long l) {
        this.idCliente = null;
        this.chiusura = Chiusura.Chiuso;
        this.stato = StatoBox.Libero;
        this.locker = l;
        this.statoAccensioneBox = Accensione.Acceso;
    }

    public void turnOnBox(){
        this.statoAccensioneBox= Accensione.Acceso;
        this.setStato(StatoBox.Libero);
    }
    public void turnOffBox(){
        this.statoAccensioneBox= Accensione.Spento;
        this.setStato(StatoBox.Occupato);
        this.setIdCliente(null);
    }


    public Long getIdBox() {
        return this.idBox;
    }

    public StatoBox getStato() {
        return this.stato;
    }

    /**
     * cambia stato, attesa --> occupato --> libero
     */
    public void avanzaStato() {
        switch (this.stato){
            case Libero: this.stato = StatoBox.Attesa; break;
            case Attesa: this.stato = StatoBox.Occupato; break;
            case Occupato: this.stato = StatoBox.Libero; break;
        }
    }

    public void unlock() {
        setChiusura(Chiusura.Aperto);
    }

    public void lock(){
        if(this.stato.equals(StatoBox.Occupato))
            this.idCliente = null;
        avanzaStato();
        setChiusura(Chiusura.Chiuso);
    }

    public void setStato(StatoBox s) {
        this.stato = s;
    }

    public void setIdBox(Long idBox) {
        this.idBox = idBox;
    }

    public Long getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
    }

    public Long getLocker() {
        return locker;
    }

    public void setLocker(Long locker) {
        this.locker = locker;
    }

    public void setChiusura(Chiusura a) {
        this.chiusura = a;
    }

    public Accensione getStatoAccensioneBox() {
        return statoAccensioneBox;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Box)) return false;
        Box box = (Box) o;
        return getIdBox().equals(box.getIdBox());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdBox());
    }

    @Override
    public String toString() {
        return "Box{" +
                "idBox=" + idBox +
                ", idCliente=" + idCliente +
                ", locker=" + locker +
                ", chiusura=" + chiusura +
                ", stato=" + stato +
                ", statoAccensioneBox=" + statoAccensioneBox +
                '}';
    }
}