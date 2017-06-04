///* FeatureIDE - A Framework for Feature-Oriented Software Development
// * Copyright (C) 2005-2016  FeatureIDE team, University of Magdeburg, Germany
// *
// * This file is part of FeatureIDE.
// * 
// * FeatureIDE is free software: you can redistribute it and/or modify
// * it under the terms of the GNU Lesser General Public License as published by
// * the Free Software Foundation, either version 3 of the License, or
// * (at your option) any later version.
// * 
// * FeatureIDE is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU Lesser General Public License for more details.
// * 
// * You should have received a copy of the GNU Lesser General Public License
// * along with FeatureIDE.  If not, see <http://www.gnu.org/licenses/>.
// *
// * See http://featureide.cs.ovgu.de/ for further information.
// */
//package de.ovgu.featureide.fm.core.cnf.analysis;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.LinkedList;
//import java.util.List;
//
//import org.prop4j.Equals;
//import org.prop4j.Literal;
//import org.prop4j.Node;
//import org.prop4j.Not;
//import org.sat4j.specs.ContradictionException;
//import org.sat4j.specs.IConstr;
//
//import de.ovgu.featureide.fm.core.ConstraintAttribute;
//import de.ovgu.featureide.fm.core.FeatureStatus;
//import de.ovgu.featureide.fm.core.Logger;
//import de.ovgu.featureide.fm.core.base.FeatureUtils;
//import de.ovgu.featureide.fm.core.base.IConstraint;
//import de.ovgu.featureide.fm.core.base.IFeature;
//import de.ovgu.featureide.fm.core.base.IFeatureModel;
//import de.ovgu.featureide.fm.core.base.IFeatureModelFactory;
//import de.ovgu.featureide.fm.core.base.impl.FMFactoryManager;
//import de.ovgu.featureide.fm.core.cnf.CNF;
//import de.ovgu.featureide.fm.core.cnf.CNFCreator;
//import de.ovgu.featureide.fm.core.cnf.CNFCreator.ModelType;
//import de.ovgu.featureide.fm.core.cnf.LiteralSet;
//import de.ovgu.featureide.fm.core.cnf.Nodes;
//import de.ovgu.featureide.fm.core.cnf.SatUtils;
//import de.ovgu.featureide.fm.core.cnf.Variables;
//import de.ovgu.featureide.fm.core.cnf.solver.AdvancedSatSolver;
//import de.ovgu.featureide.fm.core.cnf.solver.ISatSolver2;
//import de.ovgu.featureide.fm.core.cnf.solver.ISimpleSatSolver.SatResult;
//import de.ovgu.featureide.fm.core.cnf.solver.ModifiableSatSolver;
//import de.ovgu.featureide.fm.core.cnf.solver.RuntimeContradictionException;
//import de.ovgu.featureide.fm.core.filter.HiddenFeatureFilter;
//import de.ovgu.featureide.fm.core.functional.Functional;
//import de.ovgu.featureide.fm.core.job.LongRunningMethod;
//import de.ovgu.featureide.fm.core.job.LongRunningWrapper;
//import de.ovgu.featureide.fm.core.job.monitor.IMonitor;
//import de.ovgu.featureide.fm.core.job.monitor.NullMonitor;
//
///**
// * A collection of methods for working with {@link IFeatureModel} will replace
// * the corresponding methods in {@link IFeatureModel}
// * 
// * @author Soenke Holthusen
// * @author Florian Proksch
// * @author Stefan Krueger
// * @author Marcus Pinnecke (Feature Interface)
// */
//public class FeatureModelAnalysis implements LongRunningMethod<HashMap<Object, Object>> {
//	/**
//	 * Defines whether constraints should be included into calculations.
//	 */
//	public boolean calculateConstraints = true;
//
//	/**
//	 * Defines whether features should be included into calculations.
//	 * If features are not analyzed, then constraints a also NOT analyzed.
//	 */
//	public boolean calculateFeatures = true;
//
//	/**
//	 * Defines whether redundant constraints should be calculated.
//	 */
//	public boolean calculateRedundantConstraints = true;
//
//	public boolean calculateFOConstraints = true;
//
//	public boolean calculateDeadConstraints = true;
//
//	/**
//	 * Defines whether constraints that are tautologies should be calculated.
//	 */
//	public boolean calculateTautologyConstraints = true;
//
//	private final HashMap<Object, Object> changedAttributes = new HashMap<>();
//
//	private boolean valid;
//	private final List<IFeature> coreFeatures;
//	private final List<IFeature> deadFeatures;
//	private final List<IFeature> falseOptionalFeatures;
//
//	private final IFeatureModel fm;
//	private final IFeatureModelFactory factory;
//	private final CNFCreator nodeCreator;
//
//	private IMonitor monitor = new NullMonitor();
//
//	public FeatureModelAnalysis(IFeatureModel fm) {
//		this.fm = fm;
//		this.factory = FMFactoryManager.getFactory(fm);
//
//		deadFeatures = new ArrayList<>();
//		coreFeatures = new ArrayList<>();
//		falseOptionalFeatures = new ArrayList<>();
//
//		nodeCreator = new CNFCreator(fm);
//	}
//
//	public boolean isCalculateConstraints() {
//		return calculateConstraints;
//	}
//
//	public boolean isCalculateFeatures() {
//		return calculateFeatures;
//	}
//
//	public boolean isCalculateRedundantConstraints() {
//		return calculateRedundantConstraints;
//	}
//
//	public boolean isCalculateTautologyConstraints() {
//		return calculateTautologyConstraints;
//	}
//
//	public boolean isValid() {
//		return valid;
//	}
//
//	public List<IFeature> getCoreFeatures() {
//		return coreFeatures;
//	}
//
//	public List<IFeature> getDeadFeatures() {
//		return deadFeatures;
//	}
//
//	public List<IFeature> getFalseOptionalFeatures() {
//		return falseOptionalFeatures;
//	}
//
//	public void setCalculateConstraints(boolean calculateConstraints) {
//		this.calculateConstraints = calculateConstraints;
//	}
//
//	public void setCalculateFeatures(boolean calculateFeatures) {
//		this.calculateFeatures = calculateFeatures;
//	}
//
//	public void setCalculateRedundantConstraints(boolean calculateRedundantConstraints) {
//		this.calculateRedundantConstraints = calculateRedundantConstraints;
//	}
//
//	public void setCalculateTautologyConstraints(boolean calculateTautologyConstraints) {
//		this.calculateTautologyConstraints = calculateTautologyConstraints;
//	}
//
//	public void setCalculateFOConstraints(boolean calculateFOConstraints) {
//		this.calculateFOConstraints = calculateFOConstraints;
//	}
//
//	public void setCalculateDeadConstraints(boolean calculateDeadConstraints) {
//		this.calculateDeadConstraints = calculateDeadConstraints;
//	}
//
//	/**
//	 * @return Hashmap: key entry is Feature/Constraint, value usually
//	 *         indicating the kind of attribute (non-Javadoc)
//	 */
//	@Override
//	public HashMap<Object, Object> execute(IMonitor monitor) throws Exception {
//		this.monitor = monitor;
//		int work = 0;
//		if (calculateFeatures) {
//			work += 5;
//			if (calculateConstraints) {
//				work += 2;
//			}
//		}
//		monitor.setRemainingWork(work);
//
//		changedAttributes.clear();
//
//		deadFeatures.clear();
//		coreFeatures.clear();
//		falseOptionalFeatures.clear();
//
//		// put root always in so it will be refreshed (void/non-void)
//		changedAttributes.put(fm.getStructure().getRoot().getFeature(), FeatureStatus.NORMAL);
//
//		valid = true;
//
//		if (calculateFeatures) {
//			monitor.checkCancel();
//			updateFeatures();
//
//			if (calculateConstraints) {
//				monitor.checkCancel();
//				updateConstraints();
//			}
//		}
//
//		return changedAttributes;
//	}
//
//	public void updateFeatures() {
//		final Iterable<IFeature> features = fm.getFeatures();
//		for (IFeature feature : features) {
//			feature.getProperty().setFeatureStatus(FeatureStatus.NORMAL, false);
//			FeatureUtils.setRelevantConstraints(feature);
//		}
//		monitor.step();
//
//		nodeCreator.setModelType(ModelType.All);
//		final CNF si = nodeCreator.createNodes();
//
//		checkValidity(si);
//		monitor.step();
//
//		if (valid) {
//			checkFeatureFalseOptional(features, si);
//			monitor.step();
//
//			checkFeatureDead(si);
//			monitor.step();
//
//			checkFeatureHidden(features);
//			monitor.step();
//		}
//	}
//
//	public void updateConstraints() {
//		final List<IConstraint> constraints = fm.getConstraints();
//		for (IConstraint constraint : constraints) {
//			constraint.setConstraintAttribute(ConstraintAttribute.NORMAL, false);
//			constraint.setContainedFeatures();
//			constraint.setFalseOptionalFeatures(Collections.<IFeature> emptyList());
//			constraint.setDeadFeatures(Collections.<IFeature> emptyList());
//		}
//
//		if (!calculateFeatures) {
//			checkValidity(nodeCreator.createNodes());
//		}
//
//		try {
//			if (valid) {
//				checkConstraintRedundant(constraints);
//				monitor.step();
//				checkConstraintDeadAndFalseOptional(constraints);
//				monitor.step();
//			} else {
//				checkConstraintUnsatisfiable(constraints);
//				monitor.step();
//				monitor.step();
//			}
//		} catch (ContradictionException e) {
//			Logger.logError(e);
//		}
//	}
//
//	private boolean checkConstraintContradiction(Variables satInstance, List<LiteralSet> constraintNode) {
//		return LongRunningWrapper.runMethod(new HasSolutionAnalysis(new CNF(satInstance, constraintNode)));
//	}
//
//	private void checkConstraintDeadAndFalseOptional(final List<IConstraint> constraints) throws ContradictionException {
//		if (!calculateFOConstraints && !calculateDeadConstraints) {
//			return;
//		}
//		nodeCreator.setModelType(ModelType.OnlyStructure);
//		final CNF si = nodeCreator.createNodes();
//		final ISatSolver2 modSat = new AdvancedSatSolver(si);
//
//		final List<IFeature> deadList = new LinkedList<>(deadFeatures);
//		final List<IFeature> foList = new LinkedList<>(falseOptionalFeatures);
//		monitor.checkCancel();
//
//		for (IConstraint constraint : constraints) {
//			modSat.addClauses(Nodes.convert(si.getVariables(), constraint.getNode()));
//
//			if (constraint.getConstraintAttribute() == ConstraintAttribute.NORMAL) {
//				if (calculateDeadConstraints) {
//					final List<IFeature> newDeadFeature = checkFeatureDead2(modSat, deadList);
//					if (!newDeadFeature.isEmpty()) {
//						constraint.setDeadFeatures(newDeadFeature);
//						deadList.removeAll(newDeadFeature);
//						setConstraintAttribute(constraint, ConstraintAttribute.DEAD);
//					}
//				}
//
//				if (calculateFOConstraints) {
//					final List<IFeature> newFOFeature = checkFeatureFalseOptional2(modSat, foList);
//					if (!newFOFeature.isEmpty()) {
//						constraint.setFalseOptionalFeatures(newFOFeature);
//						foList.removeAll(newFOFeature);
//						if (constraint.getConstraintAttribute() == ConstraintAttribute.NORMAL) {
//							setConstraintAttribute(constraint, ConstraintAttribute.FALSE_OPTIONAL);
//						}
//					}
//				}
//			}
//			monitor.checkCancel();
//		}
//	}
//
//	/**
//	 * Detects redundancy of a constraint by checking if the model without the new (possibly redundant) constraint
//	 * implies the model with the new constraint and the other way round. If this is the case, both models are
//	 * equivalent and the constraint is redundant.
//	 * If a redundant constraint has been detected, it is explained.
//	 * 
//	 * @param constraint The constraint to check whether it is redundant
//	 */
//	private void checkConstraintRedundant(final List<IConstraint> constraints) throws ContradictionException {
//		if (calculateRedundantConstraints || calculateTautologyConstraints) {
//			nodeCreator.setModelType(ModelType.OnlyStructure);
//			final CNF si = nodeCreator.createNodes();
//			if (calculateRedundantConstraints) {
//				final IFeatureModel clone = fm.clone();
//				final ModifiableSatSolver redundantSat = new ModifiableSatSolver(si);
//
//			final List<List<IConstr>> constraintMarkers = new ArrayList<>();
//				final List<List<LiteralSet>> cnfNodes = new ArrayList<>();
//			for (IConstraint constraint : constraints) {
//				List<LiteralSet> cnf = Nodes.convert(si.getVariables(), constraint.getNode());
//				cnfNodes.add(cnf);
//
//				constraintMarkers.add(redundantSat.addClauses(cnf));
//			}
//			monitor.checkCancel();
//
//			int i = -1;
//			for (IConstraint constraint : constraints) {
//				i++;
//				if (calculateRedundantConstraints) {
//					boolean redundant = true;
//					boolean removedAtLeastOne = false;
//					for (IConstr cm : constraintMarkers.get(i)) {
//						if (cm != null) {
//							removedAtLeastOne = true;
//								redundantSat.removeClause(cm);
//						}
//					}
//					if (removedAtLeastOne) {
//							final List<LiteralSet> constraintNode = cnfNodes.get(i);
//
//							loop: for (LiteralSet clause : constraintNode) {
//								final SatResult satResult = redundantSat.hasSolution(SatUtils.negateSolution(clause.getLiterals()));
//								switch (satResult) {
//								case FALSE:
//								case TIMEOUT:
//								redundant = false;
//								redundantSat.addClauses(constraintNode);
//									break loop;
//								case TRUE:
//								break;
//								default:
//									throw new AssertionError(satResult);
//							}
//						}
//					}
//
//					if (redundant) {
//							clone.removeConstraint(constraint);
//							final List<LiteralSet> clauseList = Nodes.convert(si.getVariables(), new Not(constraint.getNode()));
//							if (checkConstraintTautology(si.getVariables(), clauseList)) {
//							setConstraintAttribute(constraint, ConstraintAttribute.TAUTOLOGY);
//						} else {
//							setConstraintAttribute(constraint, ConstraintAttribute.REDUNDANT);
//						}
//					}
//				}
//				monitor.checkCancel();
//			}
//			} else {
//			for (IConstraint constraint : constraints) {
//					final List<LiteralSet> clauseList = Nodes.convert(si.getVariables(), new Not(constraint.getNode()));
//					if (checkConstraintTautology(si.getVariables(), clauseList)) {
//					setConstraintAttribute(constraint, ConstraintAttribute.TAUTOLOGY);
//				}
//				monitor.checkCancel();
//			}
//		}
//	}
//	}
//
//	private boolean checkConstraintTautology(Variables mapping, List<LiteralSet> constraintNode) {
//		return checkConstraintContradiction(mapping, constraintNode);
//	}
//
//	private void checkConstraintUnsatisfiable(final List<IConstraint> constraints) throws ContradictionException {
//		nodeCreator.setModelType(ModelType.OnlyStructure);
//		final CNF si = nodeCreator.createNodes();
//		final ModifiableSatSolver unsat = new ModifiableSatSolver(si);
//		monitor.checkCancel();
//
//		for (IConstraint constraint : constraints) {
//			List<LiteralSet> cnf = Nodes.convert(si.getVariables(), constraint.getNode());
//
//			List<IConstr> constraintMarkers = null;
//			boolean satisfiable;
//			try {
//				constraintMarkers = unsat.addClauses(cnf);
//				satisfiable = unsat.hasSolution() == SatResult.TRUE;
//			} catch (RuntimeContradictionException e) {
//				satisfiable = false;
//			}
//
//			if (!satisfiable) {
//				if (constraintMarkers != null) {
//					for (IConstr constr : constraintMarkers) {
//						if (constr != null) {
//							unsat.removeClause(constr);
//						}
//					}
//
//					if (checkConstraintContradiction(si.getVariables(), cnf)) {
//						setConstraintAttribute(constraint, ConstraintAttribute.UNSATISFIABLE);
//					} else {
//						setConstraintAttribute(constraint, ConstraintAttribute.VOID_MODEL);
//					}
//				} else {
//					setConstraintAttribute(constraint, ConstraintAttribute.UNSATISFIABLE);
//				}
//			}
//			monitor.checkCancel();
//		}
//	}
//
//	private void checkFeatureDead(final CNF si) {
//		deadFeatures.clear();
//		coreFeatures.clear();
//		final LiteralSet solution2 = LongRunningWrapper.runMethod(new CoreDeadAnalysis(si), monitor.subTask(0));
//		monitor.checkCancel();
//		for (int i = 0; i < solution2.getLiterals().length; i++) {
//			monitor.checkCancel();
//			final int var = solution2.getLiterals()[i];
//			final IFeature feature = fm.getFeature(si.getVariables().getName(var));
//			if (var < 0) {
//				setFeatureAttribute(feature, FeatureStatus.DEAD);
//				deadFeatures.add(feature);
//			} else {
//				coreFeatures.add(feature);
//			}
//		}
//	}
//
//	private List<IFeature> checkFeatureDead2(final ISatSolver2 solver, List<IFeature> deadList) {
//		if (deadList.size() == 0) {
//			return Collections.emptyList();
//		}
//		final List<IFeature> result = new ArrayList<>();
//		int[] deadVars = new int[deadList.size()];
//		int j = 0;
//		for (IFeature deadFeature : deadList) {
//			deadVars[j++] = solver.getSatInstance().getVariables().getVariable(deadFeature.getName());
//		}
//		final LiteralSet solution2 = LongRunningWrapper.runMethod(new CoreDeadAnalysis(solver, new LiteralSet(deadVars)));
//		for (int i = 0; i < solution2.getLiterals().length; i++) {
//			final int var = solution2.getLiterals()[i];
//			if (var < 0) {
//				result.add(fm.getFeature(solver.getSatInstance().getVariables().getName(var)));
//			}
//		}
//		return result;
//	}
//
//	private void checkFeatureFalseOptional(final Iterable<IFeature> features, final CNF si) {
//		final List<LiteralSet> possibleFOFeatures = new ArrayList<>();
//		for (IFeature feature : features) {
//			final IFeature parent = FeatureUtils.getParent(feature);
//			if (parent != null && (!feature.getStructure().isMandatorySet() || !parent.getStructure().isAnd())) {
//				possibleFOFeatures.add(new LiteralSet(-si.getVariables().getVariable(parent.getName()), si.getVariables().getVariable(feature.getName())));
//			}
//		}
//		final List<LiteralSet> solution3 = LongRunningWrapper.runMethod(new RedundancyAnalysis(si, possibleFOFeatures), monitor.subTask(0));
//		monitor.checkCancel();
//		falseOptionalFeatures.clear();
//		for (LiteralSet pair : solution3) {
//			monitor.checkCancel();
//			final IFeature feature = fm.getFeature(si.getVariables().getName(pair.getLiterals()[1]));
//			setFeatureAttribute(feature, FeatureStatus.FALSE_OPTIONAL);
//			falseOptionalFeatures.add(feature);
//		}
//	}
//
//	private List<IFeature> checkFeatureFalseOptional2(final ISatSolver2 solver, List<IFeature> foList) {
//		if (foList.size() == 0) {
//			return Collections.emptyList();
//		}
//		final List<IFeature> result = new ArrayList<>();
//		final List<LiteralSet> possibleFOFeatures = new ArrayList<>();
//		final CNF si = solver.getSatInstance();
//		for (IFeature feature : foList) {
//			final IFeature parent = FeatureUtils.getParent(feature);
//			if (parent != null && (!feature.getStructure().isMandatorySet() || !parent.getStructure().isAnd())) {
//				possibleFOFeatures.add(new LiteralSet(-si.getVariables().getVariable(parent.getName()), si.getVariables().getVariable(feature.getName())));
//			}
//		}
//		final List<LiteralSet> solution3 = LongRunningWrapper.runMethod(new RedundancyAnalysis(solver, possibleFOFeatures));
//		for (LiteralSet pair : solution3) {
//			result.add(fm.getFeature(si.getVariables().getName(pair.getLiterals()[1])));
//		}
//		return result;
//	}
//
//	/**
//	 * Calculations for indeterminate hidden features
//	 * 
//	 * @param changedAttributes
//	 */
//	private void checkFeatureHidden(final Iterable<IFeature> features, final CNF si) {
//		if (!fm.getStructure().hasHidden()) {
//			return;
//		}
//
//		final Iterable<IFeature> hiddenFeatures = Functional.filter(features, new HiddenFeatureFilter());
//		List<String> hiddenLiterals = Functional.toList(Functional.map(hiddenFeatures, new Functional.IFunction<IFeature, String>() {
//			@Override
//			public String invoke(IFeature feature) {
//				return feature.getName();
//			}
//		}));
//		
//		final DeterminedAnalysis method = new DeterminedAnalysis(si, LiteralSet variables);
//		method.setAssumptions(null);
//		final LiteralSet determinedHidden = LongRunningWrapper.runMethod(method);
//		
//		for (int feature : determinedHidden) {
//			setFeatureAttribute(fm.getFeature(si.getVariableObject(feature).toString()), FeatureStatus.INDETERMINATE_HIDDEN);
//		}
//	}
//
////		/**
////		 * Additionally each Node is checked if the atomic set containing it, consists of indeterminate hidden nodes only.
////		 * If this is the case it's also indeterminate.
////		 * A node is therefore not marked indeterminate if it either
////		 * - has a non-hidden Node in its atomic set defining its state or
////		 * - if a Node of its atomic set is determined by a constraint of the above form.
////		 */
////		FeatureDependencies featureDependencies = new FeatureDependencies(fm, false);
////		for (IFeature feature : hiddenFeatures) {
////			monitor.checkCancel();
////			if (!list.contains(feature)) {
////				Collection<IFeature> set = featureDependencies.getImpliedFeatures(feature);
////				boolean noHidden = false;
////				for (IFeature f : set) {
////					if (!f.getStructure().isHidden() && !f.getStructure().hasHiddenParent() || list.contains(f)) {
////						if (featureDependencies.isAlways(f, feature)) {
////							noHidden = true;
////							break;
////						}
////					}
////				}
////
////				if (!noHidden) {
////					setFeatureAttribute(feature, FeatureStatus.INDETERMINATE_HIDDEN);
////				}
////			}
////		}
//	}
//
//	private void checkValidity(final CNF si) {
//		valid = LongRunningWrapper.runMethod(new HasSolutionAnalysis(si)) != null;
//	}
//
//	private void setFeatureAttribute(IFeature feature, FeatureStatus featureAttribute) {
//		changedAttributes.put(feature, featureAttribute);
//		feature.getProperty().setFeatureStatus(featureAttribute, false);
//	}
//
//	private void setConstraintAttribute(IConstraint constraint, ConstraintAttribute constraintAttribute) {
//		changedAttributes.put(constraint, constraintAttribute);
//		constraint.setConstraintAttribute(constraintAttribute, false);
//	}
//
//}