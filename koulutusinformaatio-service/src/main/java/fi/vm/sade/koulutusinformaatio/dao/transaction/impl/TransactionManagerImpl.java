/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * European Union Public Licence for more details.
 */

package fi.vm.sade.koulutusinformaatio.dao.transaction.impl;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.common.params.CoreAdminParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;

import fi.vm.sade.koulutusinformaatio.dao.ApplicationOptionDAO;
import fi.vm.sade.koulutusinformaatio.dao.ChildLearningOpportunityDAO;
import fi.vm.sade.koulutusinformaatio.dao.DataStatusDAO;
import fi.vm.sade.koulutusinformaatio.dao.LearningOpportunityProviderDAO;
import fi.vm.sade.koulutusinformaatio.dao.ParentLearningOpportunitySpecificationDAO;
import fi.vm.sade.koulutusinformaatio.dao.PictureDAO;
import fi.vm.sade.koulutusinformaatio.dao.entity.DataStatusEntity;
import fi.vm.sade.koulutusinformaatio.dao.transaction.TransactionManager;

/**
 * @author Mikko Majapuro
 */
@Service
public class TransactionManagerImpl implements TransactionManager {

    private MongoClient mongo;
    private final String transactionDbName;
    private final String dbName;
    private final String providerUpdateCoreName;
    private final String providerCoreName;
    private final String learningopportunityUpdateCoreName;
    private final String learningopportunityCoreName;
    private final String locationUpdateCoreName;
    private final String locationCoreName;
    private DataStatusDAO dataStatusTransactionDAO;
    private HttpSolrServer loUpdateHttpSolrServer;
    private HttpSolrServer lopUpdateHttpSolrServer;
    private HttpSolrServer locationUpdateHttpSolrServer;

    private HttpSolrServer adminHttpSolrServer;
    private ParentLearningOpportunitySpecificationDAO parentLOSTransactionDAO;
    private ApplicationOptionDAO applicationOptionTransactionDAO;
    private LearningOpportunityProviderDAO learningOpportunityProviderTransactionDAO;
    private ChildLearningOpportunityDAO childLOTransactionDAO;
    private PictureDAO pictureTransactionDAO;

    private ParentLearningOpportunitySpecificationDAO parentLearningOpportunitySpecificationDAO;
    private ApplicationOptionDAO applicationOptionDAO;
    private ChildLearningOpportunityDAO childLearningOpportunityDAO;
    private LearningOpportunityProviderDAO learningOpportunityProviderDAO;
    private DataStatusDAO dataStatusDAO;
    private PictureDAO pictureDAO;
    
    
    @Value("${solr.learningopportunity.alias.url:learning_opportunity}")
    private String loHttpAliasName;
    
    @Value("${solr.provider.alias.url:provider}")
    private String lopHttpAliasName;
    
    @Value("${solr.location.alias.url:location}")
    private String locationHttpAliasName;
    
    @Value("${solr.learningopportunity.url:learning_opportunity}")
    private String loHttpSolrName;

    @Autowired
    public TransactionManagerImpl(MongoClient mongo, @Value("${mongo.transaction-db.name}") String transactionDbName,
                                  @Value("${mongo.db.name}") String dbName, DataStatusDAO dataStatusTransactionDAO,
                                  @Qualifier("loUpdateHttpSolrServer") HttpSolrServer loUpdateHttpSolrServer,
                                  @Qualifier("lopUpdateHttpSolrServer") HttpSolrServer lopUpdateHttpSolrServer,
                                  @Qualifier("locationUpdateHttpSolrServer") HttpSolrServer locationUpdateHttpSolrServer,
                                  @Qualifier("adminHttpSolrServer") HttpSolrServer adminHttpSolrServer,
                                  @Value("${solr.provider.url}") String providerCoreName,
                                  @Value("${solr.provider.update.url}") String providerUpdateCoreName,
                                  @Value("${solr.learningopportunity.url}") String learningopportunityCoreName,
                                  @Value("${solr.learningopportunity.update.url}") String learningopportunityUpdateCoreName,
                                  @Value("${solr.location.url}") String locationCoreName,
                                  @Value("${solr.location.update.url}") String locationUpdateCoreName,
                                  ParentLearningOpportunitySpecificationDAO parentLOSTransactionDAO,
                                  ApplicationOptionDAO applicationOptionTransactionDAO,
                                  LearningOpportunityProviderDAO learningOpportunityProviderTransactionDAO,
                                  ChildLearningOpportunityDAO childLOTransactionDAO,
                                  PictureDAO pictureTransactionDAO,
                                  ParentLearningOpportunitySpecificationDAO parentLearningOpportunitySpecificationDAO,
                                  ApplicationOptionDAO applicationOptionDAO,
                                  ChildLearningOpportunityDAO childLearningOpportunityDAO,
                                  LearningOpportunityProviderDAO learningOpportunityProviderDAO,
                                  DataStatusDAO dataStatusDAO,
                                  PictureDAO pictureDAO) {

        this.mongo = mongo;
        this.transactionDbName = transactionDbName;
        this.dbName = dbName;
        this.providerCoreName = providerCoreName;
        this.providerUpdateCoreName = providerUpdateCoreName;
        this.learningopportunityUpdateCoreName = learningopportunityUpdateCoreName;
        this.learningopportunityCoreName = learningopportunityCoreName;
        this.locationCoreName = locationCoreName;
        this.locationUpdateCoreName = locationUpdateCoreName;
        this.dataStatusTransactionDAO = dataStatusTransactionDAO;
        this.loUpdateHttpSolrServer = loUpdateHttpSolrServer;
        this.lopUpdateHttpSolrServer = lopUpdateHttpSolrServer;
        this.locationUpdateHttpSolrServer = locationUpdateHttpSolrServer;
        this.adminHttpSolrServer = adminHttpSolrServer;
        this.dataStatusTransactionDAO = dataStatusTransactionDAO;
        this.parentLOSTransactionDAO = parentLOSTransactionDAO;
        this.applicationOptionTransactionDAO = applicationOptionTransactionDAO;
        this.learningOpportunityProviderTransactionDAO = learningOpportunityProviderTransactionDAO;
        this.childLOTransactionDAO = childLOTransactionDAO;
        this.pictureTransactionDAO = pictureTransactionDAO;
        this.parentLearningOpportunitySpecificationDAO = parentLearningOpportunitySpecificationDAO;
        this.applicationOptionDAO = applicationOptionDAO;
        this.childLearningOpportunityDAO = childLearningOpportunityDAO;
        this.learningOpportunityProviderDAO = learningOpportunityProviderDAO;
        this.dataStatusDAO = dataStatusDAO;
        this.pictureDAO = pictureDAO;
    }

    @Override
    public void beginTransaction(HttpSolrServer loUpdateSolr, HttpSolrServer lopUpdateSolr, HttpSolrServer locationUpdateSolr) {
        dropUpdateData(loUpdateSolr, lopUpdateSolr, locationUpdateSolr);
    }

    @Override
    public void rollBack(HttpSolrServer loUpdateSolr, HttpSolrServer lopUpdateSolr, HttpSolrServer locationUpdateSolr) {
        dropUpdateData(loUpdateSolr, lopUpdateSolr, locationUpdateSolr);
    }

    	@Override
        public void commit(HttpSolrServer loUpdateSolr, HttpSolrServer lopUpdateSolr, HttpSolrServer locationUpdateSolr) throws IOException, SolrServerException {
        	
        	if (this.loHttpAliasName.equals(this.loHttpSolrName)) {
        		CoreAdminRequest lopCar = getCoreSwapRequest(providerUpdateCoreName, providerCoreName);
        		lopCar.process(adminHttpSolrServer);

        		CoreAdminRequest loCar = getCoreSwapRequest(learningopportunityUpdateCoreName, learningopportunityCoreName);
        		loCar.process(adminHttpSolrServer);
        		
        		CoreAdminRequest locationCar = getCoreSwapRequest(locationUpdateCoreName, locationCoreName);
                locationCar.process(adminHttpSolrServer);
        		
        	} else {
        		swapAliases(loUpdateSolr, lopUpdateSolr, locationUpdateSolr);	
        	}

        dataStatusTransactionDAO.save(new DataStatusEntity());
        BasicDBObject cmd = new BasicDBObject("copydb", 1).append("fromdb", transactionDbName).append("todb", dbName);
        dropDbCollections();
        mongo.getDB("admin").command(cmd);
        dropTransactionDbCollections();
    }
    	
    	private void dropUpdateData(HttpSolrServer loUpdateSolr, HttpSolrServer lopUpdateSolr, HttpSolrServer locationUpdateSolr) {
            try {
                mongo.dropDatabase(transactionDbName);
                
                loUpdateSolr.deleteByQuery("*:*");
                loUpdateSolr.commit();
                loUpdateSolr.optimize();
                lopUpdateSolr.deleteByQuery("*:*");
                lopUpdateSolr.commit();
                lopUpdateSolr.optimize();
                
                locationUpdateHttpSolrServer.deleteByQuery("*:*");
                locationUpdateHttpSolrServer.commit();
                locationUpdateHttpSolrServer.optimize();
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    private void dropTransactionDbCollections() {
        parentLOSTransactionDAO.getCollection().drop();
        applicationOptionTransactionDAO.getCollection().drop();
        learningOpportunityProviderTransactionDAO.getCollection().drop();
        childLOTransactionDAO.getCollection().drop();
        pictureTransactionDAO.getCollection().drop();
    }

    private void dropDbCollections() {
        parentLearningOpportunitySpecificationDAO.getCollection().drop();
        applicationOptionDAO.getCollection().drop();
        childLearningOpportunityDAO.getCollection().drop();
        dataStatusDAO.getCollection().drop();
        pictureDAO.getCollection().drop();
        learningOpportunityProviderDAO.getCollection().drop();
    }

    private CoreAdminRequest getCoreSwapRequest(final String fromCore, final String toCore) {
        CoreAdminRequest car = new CoreAdminRequest();
        car.setCoreName(fromCore);
        car.setOtherCoreName(toCore);
        car.setAction(CoreAdminParams.CoreAdminAction.SWAP);
        return car;
    }
    
    private void swapAliases(HttpSolrServer loUpdateSolr, HttpSolrServer lopUpdateSolr, HttpSolrServer locationUpdateSolr) {
    	
        try {
            URL myURL = new URL(adminHttpSolrServer.getBaseURL() + "/admin/collections?action=CREATEALIAS&name=" + this.lopHttpAliasName +"&collections=" + getCollectionName(lopUpdateSolr));
            HttpURLConnection myURLConnection = (HttpURLConnection)(myURL.openConnection());
            myURLConnection.setRequestMethod("GET");
            myURLConnection.connect();
           
            myURL = new URL(adminHttpSolrServer.getBaseURL() + "/admin/collections?action=CREATEALIAS&name=" + this.loHttpAliasName +"&collections=" + getCollectionName(loUpdateSolr));
            myURLConnection = (HttpURLConnection)(myURL.openConnection());
            myURLConnection.setRequestMethod("GET");
            myURLConnection.connect();
            
            myURL = new URL(adminHttpSolrServer.getBaseURL() + "/admin/collections?action=CREATEALIAS&name=" + this.locationHttpAliasName +"&collections=" + getCollectionName(locationUpdateSolr));
            myURLConnection = (HttpURLConnection)(myURL.openConnection());
            myURLConnection.setRequestMethod("GET");
            myURLConnection.connect();
            
        } 
        catch (MalformedURLException e) { 
           e.printStackTrace();
        } 
        catch (IOException e) {   
            e.printStackTrace();
        }
		
	}

	private String getCollectionName (HttpSolrServer solrServer) {
    	return solrServer.getBaseURL().substring(solrServer.getBaseURL().lastIndexOf('/') + 1);
    }
}
