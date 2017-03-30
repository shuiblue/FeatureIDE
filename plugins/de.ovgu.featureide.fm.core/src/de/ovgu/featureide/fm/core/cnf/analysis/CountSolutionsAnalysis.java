/* FeatureIDE - A Framework for Feature-Oriented Software Development
 * Copyright (C) 2005-2016  FeatureIDE team, University of Magdeburg, Germany
 *
 * This file is part of FeatureIDE.
 * 
 * FeatureIDE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * FeatureIDE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with FeatureIDE.  If not, see <http://www.gnu.org/licenses/>.
 *
 * See http://featureide.cs.ovgu.de/ for further information.
 */
package de.ovgu.featureide.fm.core.cnf.analysis;

import org.sat4j.specs.TimeoutException;
import org.sat4j.tools.SolutionCounter;

import de.ovgu.featureide.fm.core.cnf.CNF;
import de.ovgu.featureide.fm.core.cnf.solver.ISatSolver2;
import de.ovgu.featureide.fm.core.job.monitor.IMonitor;

/**
 * Determines whether a sat instance is satisfiable and returns the found model.
 * 
 * @author Sebastian Krieter
 */
public class CountSolutionsAnalysis extends AbstractAnalysis<Long> {

	public CountSolutionsAnalysis(ISatSolver2 solver) {
		super(solver);
	}

	public CountSolutionsAnalysis(CNF satInstance) {
		super(satInstance);
	}

	public Long analyze(IMonitor monitor) throws Exception {
		long number = 0;
		final SolutionCounter counter = new SolutionCounter(solver.getInternalSolver());

		try {
			number = counter.countSolutions();
		} catch (TimeoutException e) {
			number = -1 - counter.lowerBound();
		}
		return number;
	}

}