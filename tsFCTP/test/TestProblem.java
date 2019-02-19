import static org.junit.jupiter.api.Assertions.*;

import dlpuertaj.optimization.domain.tsFCTP;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class TestProblem {
	private final String INSTANCE = "223";
	private tsFCTP problem;

	public tsFCTP getProblem() {
		return problem;
	}
	
	@BeforeEach
	void ini() {
		this.problem = new tsFCTP(INSTANCE);
	}
	@Test
	void testProblemInstance() {
		assertNotNull(problem);
	}
	
	@Test
	void testBuildInstance() {
		
		problem.build(INSTANCE);
		
		assertTrue(this.problem.totalDemand >= this.problem.totalProductionCapacity);
		
		problem.showProblemInstance();		
	}

}
