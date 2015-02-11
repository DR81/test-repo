package com.sample;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.Results;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.conf.ClockTypeOption;

public class Test {

	static Runtime runtime = Runtime.getRuntime();
	
	public static void main(String[] args) throws Exception {
		long start = System.currentTimeMillis();
	    long timeDrools = 0;
	    Random rand = new Random();
	    Integer randomNum = 0;
	    Integer randomEvent = 0;
	    int maxCount = 500000;
	    int maxRand = maxCount/1;
	    int minRand = 1;
	    rand = new Random();
	    
	    memory();
	    
	    System.out.println("before event drools ------------------------");
		

		KieServices kieServices = KieServices.Factory.get();
		KieModuleModel kieModuleModel = kieServices.newKieModuleModel();
		
		KieBaseModel kieBaseModel1 = kieModuleModel.newKieBaseModel( "rules")
    	        .setDefault( true )
    	        .setEqualsBehavior( EqualityBehaviorOption.EQUALITY )
    	        .setEventProcessingMode( EventProcessingOption.STREAM );

		kieBaseModel1.newKieSessionModel( "KSession1" )
    	        .setDefault( true )
    	        .setType( KieSessionModel.KieSessionType.STATEFUL )
    	        .setClockType( ClockTypeOption.get("realtime") );

		KieFileSystem kfs = kieServices.newKieFileSystem();
		ReleaseId ri = kieServices.newReleaseId( "org.default", "artifact", "1.0.0" );
		kfs.generateAndWritePomXML(ri);
		
		kfs.writeKModuleXML(kieModuleModel.toXML());
		
		FileInputStream fis = new FileInputStream( "src/main/resources/Hal1.drl" );
		kfs.write( "src/main/resources/Hal1.drl", kieServices.getResources().newInputStreamResource( fis ) );
		KieBuilder kieBuilder = kieServices.newKieBuilder( kfs ).buildAll();
		Results results = kieBuilder.getResults();
	    if( results.hasMessages( Message.Level.ERROR ) ){
	        System.out.println( results.getMessages() );
	        throw new IllegalStateException( "### errors ###" );
	    }
	    KieContainer kieContainer = kieServices.newKieContainer( ri );

	    
	    KieSession kSession = kieContainer.newKieSession("KSession1");
	    kSession.setGlobal("out", System.out);
	    
	    memory();
	    System.out.println("start events ------------------------");
	    
	    start = System.currentTimeMillis();
	    for(int i = 0; i < maxCount; i++) {
	    	randomNum = rand.nextInt((maxRand - minRand) + 1) + minRand;
	    	randomEvent = rand.nextInt((10 - 1) + 1) + 1;
	    	kSession.insert(new MessageReceived(String.valueOf(randomEvent), randomNum.toString()));
	    	kSession.fireAllRules();
	    	if(i % 100000 == 0){
	    		System.out.println("DROOLS time for "+i+" interactions with "+maxRand+" different msisdn in : "+ (System.currentTimeMillis()-start) + " ms");
	    		memory();
	    	}
	    }
	    System.out.println("after "+maxCount+" events ------------------------");
	    memory();
	    timeDrools = System.currentTimeMillis() - start;
	    
	    start = System.currentTimeMillis();
	    for(int i = 0; i < maxCount/100; i++) {
	    	randomNum = rand.nextInt((maxRand - minRand) + 1) + minRand;
	    	kSession.insert(new MessageReceived("aa"+i, randomNum.toString()));
	    	kSession.fireAllRules();
	    }
	    
	    kSession.dispose();
	    
	    System.out.println("DROOLS time for "+maxCount+" interactions with "+maxRand+" different msisdn in : "+ timeDrools + " ms");
	    
	}
	
		public static void memory() {
			//Getting the runtime reference from system
	        
	        int mb = 1024*1024;
	        
	        System.out.println("Max Memory:" + runtime.maxMemory() / mb + 
	        		" Total Memory:" + runtime.totalMemory() / mb + 
	        		" Free Memory:" + runtime.freeMemory() / mb + 
	        		"Used Memory:" + (runtime.totalMemory() - runtime.freeMemory()) / mb);
		}
	

}
