package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.BeforeClass;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import evolution.dlpuertaj.TwoStageFlowNetwork;
import evolution.dlpuertaj.tsFCTP;
import evolution.dlpuertaj.utils.Distributor;

class TestDistributor {

	public final String INSTANCE = "223";
	public tsFCTP problem = null; 
	public TwoStageFlowNetwork network = null;
	
	@BeforeClass
	public void init() {
		// TODO: unit test of problem creation
		this.problem = new tsFCTP(INSTANCE);
		this.network = new TwoStageFlowNetwork(this.problem);
	}
	
	@Test
	void startProductionTest() {
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
