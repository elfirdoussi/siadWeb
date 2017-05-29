/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import controller.util.FileGenerator;
import entities.ProblemePost;
import entities.ProblemePre;
import entities.Solution;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 *
 * @author AIMAD
 */
@Stateless
public class ProblemePostFacade extends AbstractFacade<ProblemePost> {

    @PersistenceContext(unitName = "siadWebPU")
    private EntityManager em;

    @EJB
    private FileGenerator fileGenerator;
    @EJB
    private ConfigFacade configFacade;
//    @EJB
//    private SolutionFacade solutionFacade;

    public int createProblemePost(ProblemePost problemePost, String generationDirectory) throws FileNotFoundException {
        int code = verifyAddProblemePost(problemePost);
        if (code > 0) {
            ProblemePost p = findByProblemePre(problemePost.getProblemePre());
            System.out.println("haaaaa pp "+p.getId());
            if (p == null) {
                create(problemePost);
//                fileGenerator.generate(problemePost.getProblemePre(), findByProblemePre(problemePost.getProblemePre()));
            } else {
//                try {
//                    solutionFacade.exportSolution(p, generationDirectory);
                    p.setCritereOptimisation(problemePost.getCritereOptimisation());
                    edit(p);
//                } 
//                catch (IOException ex) {
//                    Logger.getLogger(ProblemePostFacade.class.getName()).log(Level.SEVERE, null, ex);
//                }
            }

        }
        return code;
    }

    public int verifyAddProblemePost(ProblemePost problemePost) {
        if (problemePost.getProblemePre() == null) {
            return -1;
        }
        if (problemePost.getCritereOptimisation() == null) {
            return -2;
        }
        return 1;
    }

    public ProblemePost findByProblemePre(ProblemePre problemePre) {
        ProblemePost problemePost = null;
        try {
            problemePost = (ProblemePost) getEntityManager().createQuery("select p from ProblemePost p where p.problemePre.nump=" + problemePre.getNump()).getSingleResult();
        } catch (NoResultException e) {
        }
        return problemePost;
    }

    public void importResultat(ProblemePost problemePost) {
        List<String> lines = new ArrayList<>();
        Solution solution = new Solution();
        solution.setProblemePost(problemePost);
        try {
            FileReader fr = new FileReader(configFacade.getDataFilePath() + "\\result1.txt");
            BufferedReader br = new BufferedReader(fr);
            String line = null;
            while ((line = br.readLine()) != null) {
                solution.setDataa(solution.getDataa()+"@"+line);
            }
            br.close();
            fr.close();
        } catch (IOException e) {
            System.out.println("Fichier non trouve");
        }
        
//        solutionFacade.create(solution);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ProblemePostFacade() {
        super(ProblemePost.class);
    }

}
