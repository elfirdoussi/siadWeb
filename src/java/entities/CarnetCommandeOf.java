package entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;
import controller.util.DateUtil;

/**
 *
 * @author Aimad.JAOUHAR
 */

@Entity
public class CarnetCommandeOf implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	private Long numeroOf;
	@ManyToOne
	private CarnetCommande carnetCommande;
	@ManyToOne(fetch = FetchType.EAGER)
	private Engrais engrais;
	private Float quantite;
	private String client;
	private Float ponderation;
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateLiveTot;
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateLiveTard;
	private boolean encours;
	@ManyToOne
	private Ligne ligne;

	

	public CarnetCommandeOf() {
		super();
	}
	
	public String getTitle(){
		return (numeroOf == null ? "": numeroOf)  +" : "+ (engrais == null ? "-" : engrais.getLibelle());
	}

	public Long getNumeroOf() {
		return numeroOf;
	}

	public void setNumeroOf(Long numeroOf) {
		this.numeroOf = numeroOf;
	}

	public CarnetCommande getCarnetCommande() {
		return carnetCommande;
	}

	public void setCarnetCommande(CarnetCommande carnetCommande) {
		this.carnetCommande = carnetCommande;
	}

	public Engrais getEngrais() {
		return engrais;
	}

	public void setEngrais(Engrais engrais) {
		this.engrais = engrais;
	}

	public Float getQuantite() {
		return quantite;
	}

	public void setQuantite(Float quantite) {
		this.quantite = quantite;
	}

	public String getClient() {
		return client;
	}

	public void setClient(String client) {
		this.client = client;
	}

	public Float getPonderation() {
		return ponderation;
	}

	public void setPonderation(Float ponderation) {
		this.ponderation = ponderation;
	}

	public Date getDateLiveTot() {
		return dateLiveTot;
	}

	public void setDateLiveTot(Date dateLiveTot) {
		this.dateLiveTot = dateLiveTot;
	}

	public Date getDateLiveTard() {
		return dateLiveTard;
	}

	public void setDateLiveTard(Date dateLiveTard) {
		this.dateLiveTard = dateLiveTard;
	}

	public boolean getEncours() {
		return encours;
	}

	public void setEncours(boolean encours) {
		this.encours = encours;
	}

	public Ligne getLigne() {
		return ligne;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setLigne(Ligne ligne) {
		this.ligne = ligne;
	}
	
	public String getDateLiveTotStr() {
		return dateLiveTot != null ? DateUtil.strDateDefaultPattern(dateLiveTot): "";
	}


	public String getDateLiveTardStr() {
		return dateLiveTard != null ? DateUtil.strDateDefaultPattern(dateLiveTard) : "";
	}

	
   
}
