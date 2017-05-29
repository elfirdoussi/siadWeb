/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author YOUNESS
 */
@Entity
public class User implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String username;
    private String password;
    private String nom;
    private String prenom;
    private String mail;
    private int nbreCnx;
    private boolean blocked;
    private boolean superAdmin;

    private boolean readFertiliezr;
    private boolean createFertiliezr;
    private boolean editFertiliezr;
    private boolean removeFertiliezr;

    private boolean readLineType;
    private boolean createLineType;
    private boolean editLineType;
    private boolean removeLineType;

    private boolean readLine;
    private boolean createLine;
    private boolean editLine;
    private boolean removeLine;

    private boolean readBom;
    private boolean createBom;
    private boolean editBom;
    private boolean removeBom;

    private boolean readRouting;
    private boolean createRouting;
    private boolean editRouting;
    private boolean removeRouting;

    private boolean readLaunch;
    private boolean createLaunch;
    private boolean editLaunch;
    private boolean removeLaunch;

    private boolean readOrderBook;
    private boolean createOrderBook;
    private boolean editOrderBook;
    private boolean removeOrderBook;

    private boolean readProblemPre;
    private boolean createProblemPre;
    private boolean editProblemPre;
    private boolean removeProblemPre;

    private boolean readProblemPost;
    private boolean createProblemPost;
    private boolean editProblemPost;
    private boolean removeProblemPost;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public int getNbreCnx() {
        return nbreCnx;
    }

    public void setNbreCnx(int nbreCnx) {
        this.nbreCnx = nbreCnx;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public boolean isSuperAdmin() {
        return superAdmin;
    }

    public void setSuperAdmin(boolean superAdmin) {
        this.superAdmin = superAdmin;
    }

    public boolean isReadFertiliezr() {
        return readFertiliezr;
    }

    public void setReadFertiliezr(boolean readFertiliezr) {
        this.readFertiliezr = readFertiliezr;
    }

    public boolean isCreateFertiliezr() {
        return createFertiliezr;
    }

    public void setCreateFertiliezr(boolean createFertiliezr) {
        this.createFertiliezr = createFertiliezr;
    }

    public boolean isEditFertiliezr() {
        return editFertiliezr;
    }

    public void setEditFertiliezr(boolean editFertiliezr) {
        this.editFertiliezr = editFertiliezr;
    }

    public boolean isRemoveFertiliezr() {
        return removeFertiliezr;
    }

    public void setRemoveFertiliezr(boolean removeFertiliezr) {
        this.removeFertiliezr = removeFertiliezr;
    }

    public boolean isReadLineType() {
        return readLineType;
    }

    public void setReadLineType(boolean readLineType) {
        this.readLineType = readLineType;
    }

    public boolean isCreateLineType() {
        return createLineType;
    }

    public void setCreateLineType(boolean createLineType) {
        this.createLineType = createLineType;
    }

    public boolean isEditLineType() {
        return editLineType;
    }

    public void setEditLineType(boolean editLineType) {
        this.editLineType = editLineType;
    }

    public boolean isRemoveLineType() {
        return removeLineType;
    }

    public void setRemoveLineType(boolean removeLineType) {
        this.removeLineType = removeLineType;
    }

    public boolean isReadLine() {
        return readLine;
    }

    public void setReadLine(boolean readLine) {
        this.readLine = readLine;
    }

    public boolean isCreateLine() {
        return createLine;
    }

    public void setCreateLine(boolean createLine) {
        this.createLine = createLine;
    }

    public boolean isEditLine() {
        return editLine;
    }

    public void setEditLine(boolean editLine) {
        this.editLine = editLine;
    }

    public boolean isRemoveLine() {
        return removeLine;
    }

    public void setRemoveLine(boolean removeLine) {
        this.removeLine = removeLine;
    }

    public boolean isReadBom() {
        return readBom;
    }

    public void setReadBom(boolean readBom) {
        this.readBom = readBom;
    }

    public boolean isCreateBom() {
        return createBom;
    }

    public void setCreateBom(boolean createBom) {
        this.createBom = createBom;
    }

    public boolean isEditBom() {
        return editBom;
    }

    public void setEditBom(boolean editBom) {
        this.editBom = editBom;
    }

    public boolean isRemoveBom() {
        return removeBom;
    }

    public void setRemoveBom(boolean removeBom) {
        this.removeBom = removeBom;
    }

    public boolean isReadRouting() {
        return readRouting;
    }

    public void setReadRouting(boolean readRouting) {
        this.readRouting = readRouting;
    }

    public boolean isCreateRouting() {
        return createRouting;
    }

    public void setCreateRouting(boolean createRouting) {
        this.createRouting = createRouting;
    }

    public boolean isEditRouting() {
        return editRouting;
    }

    public void setEditRouting(boolean editRouting) {
        this.editRouting = editRouting;
    }

    public boolean isRemoveRouting() {
        return removeRouting;
    }

    public void setRemoveRouting(boolean removeRouting) {
        this.removeRouting = removeRouting;
    }

    public boolean isReadLaunch() {
        return readLaunch;
    }

    public void setReadLaunch(boolean readLaunch) {
        this.readLaunch = readLaunch;
    }

    public boolean isCreateLaunch() {
        return createLaunch;
    }

    public void setCreateLaunch(boolean createLaunch) {
        this.createLaunch = createLaunch;
    }

    public boolean isEditLaunch() {
        return editLaunch;
    }

    public void setEditLaunch(boolean editLaunch) {
        this.editLaunch = editLaunch;
    }

    public boolean isRemoveLaunch() {
        return removeLaunch;
    }

    public void setRemoveLaunch(boolean removeLaunch) {
        this.removeLaunch = removeLaunch;
    }

    public boolean isReadOrderBook() {
        return readOrderBook;
    }

    public void setReadOrderBook(boolean readOrderBook) {
        this.readOrderBook = readOrderBook;
    }

    public boolean isCreateOrderBook() {
        return createOrderBook;
    }

    public void setCreateOrderBook(boolean createOrderBook) {
        this.createOrderBook = createOrderBook;
    }

    public boolean isEditOrderBook() {
        return editOrderBook;
    }

    public void setEditOrderBook(boolean editOrderBook) {
        this.editOrderBook = editOrderBook;
    }

    public boolean isRemoveOrderBook() {
        return removeOrderBook;
    }

    public void setRemoveOrderBook(boolean removeOrderBook) {
        this.removeOrderBook = removeOrderBook;
    }

    public boolean isReadProblemPre() {
        return readProblemPre;
    }

    public void setReadProblemPre(boolean readProblemPre) {
        this.readProblemPre = readProblemPre;
    }

    public boolean isCreateProblemPre() {
        return createProblemPre;
    }

    public void setCreateProblemPre(boolean createProblemPre) {
        this.createProblemPre = createProblemPre;
    }

    public boolean isEditProblemPre() {
        return editProblemPre;
    }

    public void setEditProblemPre(boolean editProblemPre) {
        this.editProblemPre = editProblemPre;
    }

    public boolean isRemoveProblemPre() {
        return removeProblemPre;
    }

    public void setRemoveProblemPre(boolean removeProblemPre) {
        this.removeProblemPre = removeProblemPre;
    }

    public boolean isReadProblemPost() {
        return readProblemPost;
    }

    public void setReadProblemPost(boolean readProblemPost) {
        this.readProblemPost = readProblemPost;
    }

    public boolean isCreateProblemPost() {
        return createProblemPost;
    }

    public void setCreateProblemPost(boolean createProblemPost) {
        this.createProblemPost = createProblemPost;
    }

    public boolean isEditProblemPost() {
        return editProblemPost;
    }

    public void setEditProblemPost(boolean editProblemPost) {
        this.editProblemPost = editProblemPost;
    }

    public boolean isRemoveProblemPost() {
        return removeProblemPost;
    }

    public void setRemoveProblemPost(boolean removeProblemPost) {
        this.removeProblemPost = removeProblemPost;
    }
    
    

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof User)) {
            return false;
        }
        User other = (User) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.User[ id=" + id + " ]";
    }

}
