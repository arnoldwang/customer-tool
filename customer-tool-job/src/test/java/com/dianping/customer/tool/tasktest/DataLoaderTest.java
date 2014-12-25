package com.dianping.customer.tool.tasktest;

import com.salesforce.dataloader.action.visitor.PartnerQueryVisitor;
import com.salesforce.dataloader.config.Config;
import com.sforce.soap.partner.QueryResult;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.sforce.soap.partner.sobject.SObject;
import com.salesforce.dataloader.controller.Controller;

/**
 * Created by zaza on 14/12/23.
 */
public class DataLoaderTest {

    private Controller controller;

    @Before
    public void setUp() throws Exception{
        controller = Controller.getInstance("ui", false);

    }

    @After
    public void clean() throws Exception{

    }

    @Test
    public void dateLoaderTest(){
        try{
           //controller.createAndShowGUI();
            Config config = controller.getConfig();
            config.putValue("sfdc.username","siqin.liu@dianping.com");
            config.putValue("sfdc.password","DpCRM1234");
            config.putValue("sfdc.endpoint","https://dper.my.salesforce.com");

            controller.login();
            QueryResult qr = controller.getPartnerClient().query("select Id,TerritoryID__c,UserID__c from EmployeeTerritory__c limit 5");
            SObject[] sfdcResults = qr.getRecords();

            if(controller.isLoggedIn()){
               System.out.printf("login in");
           }else{
               System.out.printf("login out");
           }


        }catch(Exception ex){
            System.out.printf(ex.getMessage());
        }

    }
}
