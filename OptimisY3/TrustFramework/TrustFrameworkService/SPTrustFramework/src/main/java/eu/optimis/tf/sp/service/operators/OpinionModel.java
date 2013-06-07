/**

Copyright 2013 ATOS SPAIN S.A. and City University London

Licensed under the Apache License, Version 2.0 (the License);
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Authors :
Pramod Pawar. City University London
Juan Luis Prieto, Francisco Javier Nieto. Atos Research and Innovation, Atos SPAIN SA
@email francisco.nieto@atosresearch.eu 

**/

package eu.optimis.tf.sp.service.operators;

public class OpinionModel {
	Opinion opinionC;
	private double belief;
	private double disBelief;
	private double unCertainty;
	private double relativeAtomicity;

	public Opinion concensus(Opinion opinionA, Opinion opinionB) {

		double K;
		K = opinionA.getUnCertainty() + opinionB.getUnCertainty()
				- opinionA.getUnCertainty() * opinionB.getUnCertainty();
		this.belief = (opinionA.getBelief() * opinionB.getUnCertainty() + opinionB
				.getBelief() * opinionA.getUnCertainty())
				/ K;

		this.disBelief = (opinionA.getDisBelief() * opinionB.getUnCertainty() + opinionB
				.getDisBelief() * opinionA.getUnCertainty())
				/ K;

		this.unCertainty = opinionA.getUnCertainty()
				* opinionB.getUnCertainty() / K;

		if (opinionA.getUnCertainty() == 1 && opinionB.getUnCertainty() == 1) {
			this.relativeAtomicity = (opinionA.getRelativeAtomicity() + opinionB
					.getRelativeAtomicity()) / 2;
		} else {
			this.relativeAtomicity = (opinionB.getRelativeAtomicity()
					* opinionA.getUnCertainty()
					+ opinionA.getRelativeAtomicity()
					* opinionB.getUnCertainty() - (opinionA
					.getRelativeAtomicity() + opinionB.getRelativeAtomicity())
					* opinionA.getUnCertainty() * opinionB.getUnCertainty())
					/ (opinionA.getUnCertainty() + opinionB.getUnCertainty() - 2
							* opinionA.getUnCertainty()
							* opinionB.getUnCertainty());
		}
//		 System.out.println("Concensus->Belief: " + this.belief);
//		 System.out.println("Concensus->DisBelief: " + this.disBelief);
//		 System.out.println("Concensus->Uncertainty :"+ this.unCertainty);
//		 System.out.println("Concensus->RelativeAtomicity :" +
//		 this.relativeAtomicity);

		opinionA.setBelief(this.belief);
		opinionA.setDisBelief(this.disBelief);
		opinionA.setUnCertainty(this.unCertainty);
		opinionA.setRelativeAtomicity(this.relativeAtomicity);
		opinionA.setExpectation();
		return opinionA;

	}

	public Opinion conjuntion(Opinion opinionA, Opinion opinionB) {

		this.belief = opinionA.getBelief() * opinionB.getBelief();

		this.disBelief = opinionA.getDisBelief() + opinionB.getDisBelief()
				- opinionA.getDisBelief() * opinionB.getDisBelief();

		this.unCertainty = opinionA.getBelief() * opinionB.getUnCertainty()
				+ opinionA.getUnCertainty() * opinionB.getBelief()
				+ opinionA.getUnCertainty() * opinionB.getUnCertainty();

		this.relativeAtomicity = (opinionA.getBelief()
				* opinionB.getUnCertainty() * opinionB.getRelativeAtomicity()
				+ opinionA.getUnCertainty() * opinionA.getRelativeAtomicity()
				* opinionB.getBelief() + opinionA.getUnCertainty()
				* opinionA.getRelativeAtomicity() * opinionB.getUnCertainty()
				* opinionB.getRelativeAtomicity())
				/ (opinionA.getBelief() * opinionB.getUnCertainty()
						+ opinionA.getUnCertainty() * opinionB.getBelief() + opinionA
						.getUnCertainty() * opinionB.getUnCertainty());
//		System.out.println("Conjunction->Belief: " + this.belief);
//		System.out.println("Conjunction->DisBelief: " + this.disBelief);
//		System.out.println("Conjunction->Uncertainty :" + this.unCertainty);
//		System.out.println("Conjunction->RelativeAtomicity :"
//				+ this.relativeAtomicity);

		// The below print statemetents are to check relativeAtomicity
		// System.out.println("a :" +opinionA.getBelief() *
		// opinionB.getUnCertainty() *
		// opinionB.getRelativeAtomicity() );
		// System.out.println("b :" + opinionA.getUnCertainty() *
		// opinionA.getRelativeAtomicity() *
		// opinionB.getBelief());
		// System.out.println("c :" + opinionA.getUnCertainty() *
		// opinionA.getRelativeAtomicity() *
		// opinionB.getUnCertainty() * opinionB.getRelativeAtomicity() );

		// System.out.println("denominator :" + (opinionA.getBelief() *
		// opinionB.getUnCertainty() +
		// opinionA.getUnCertainty() * opinionB.getBelief() +
		// opinionA.getUnCertainty() * opinionB.getUnCertainty() ) );

		opinionA.setBelief(this.belief);
		opinionA.setDisBelief(this.disBelief);
		opinionA.setUnCertainty(this.unCertainty);
		opinionA.setRelativeAtomicity(this.relativeAtomicity);
		opinionA.setExpectation();
		return opinionA;
	}

}