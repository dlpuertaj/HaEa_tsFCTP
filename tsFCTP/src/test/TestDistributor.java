package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import evolution.dlpuertaj.TwoStageFlowNetwork;
import evolution.dlpuertaj.tsFCTP;
import evolution.dlpuertaj.utils.Distributor;

class TestDistributor {

	private String INSTANCE = "223";
	private tsFCTP problem;
	private TwoStageFlowNetwork network;
	
	@BeforeEach
	public void init() {
		problem = new tsFCTP(INSTANCE);
		network = new TwoStageFlowNetwork(this.problem);
	}
	
	@Test
	void startProductionTest() {
	
		System.out.println("Problem: "+problem.I+" - "+problem.J+" - "+problem.K);
		
		assertNotNull(this.problem);
		assertNotNull(this.network);
		
		int initialProduction = 0;
		for(int c : this.network.quantityProduced) {
			initialProduction += c;
		}
		assertEquals(0, initialProduction);
		 
		Distributor.startProduction(this.network);
		
		initialProduction = 0;
		for(int c : this.network.quantityProduced) {
			initialProduction += c;
		}
		assertTrue(initialProduction > 0);
		//TODO: test production capacity
		
	}

}
