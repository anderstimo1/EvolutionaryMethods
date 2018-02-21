import java.util.ArrayList;

import nz.ac.vuw.kol.*;

public class evolutionaryMethods {

	public static int popSize = 1000;
	public static int numGenerations = 200000;
	public static int generationCount = 0;

	public static int n = numGenerations;


	public static double[] mutate (double[] d) {

		int rand = (int) (1 + Math.random() * 5);	//selects a gene to mutate
		int n = 0;

		for (int i = 0; i < d.length; i++) {

			double mut = -(n/2) + Math.random() * n;	//generates a mutation factor (the amount to mutate by)
			if (i == rand) {
				d[i] += mut;	//mutates a number in the gene
			}
		}

		return d;

	}

	public static ArrayList<double[]> mutationRecombination (double[] d, double[] e) {

		int recombFactor = (int) (1 + Math.random() * 4);	//generates the number of genes to recombine
		int start = (int) (1 + Math.random() * (5-recombFactor));	//generates the first gene to recombine - only consecutive genes can be recombined

		for (int i = start; i < d.length; i++) {
			//recombines the individuals' genes
			double temp = d[i];
			d[i] = e[i];
			e[i] = temp;
		}

		d = mutate(d);
		e = mutate(e);

		ArrayList<double[]> storage = new ArrayList<double[]>();
		storage.add(d);
		storage.add(e);

		return storage;
	}

	public static ArrayList<double[]> basicRecombination (double[] d, double[] e) {

		int rand = (int) (1 + Math.random() * (d.length-1));	//decides which number to recombine

		for(int i = 0; i < d.length; i++) {	//loops through the gene
			if (i == rand) {
				//recombines the genes
				double temp = d[i];
				d[i] = e[i];
				e[i] = temp;
			}
		}

		//mutates the two genes
		mutate(d);
		mutate(e);

		ArrayList<double[]> storage = new ArrayList<double[]>();
		storage.add(d);
		storage.add(e);

		return storage;

	}

	public static ArrayList<double[]> getTopPercent (ArrayList<double[]> population) {

		double lowest = Integer.MAX_VALUE;
		int bestGene = 0;
		ArrayList<double[]> survivors = new ArrayList<double[]> ();
		int popPercentage = (int) (population.size() * 0.2);

		for (int h = 0; h < popPercentage; h++) { //for every parent we want to get

			for (int i = 0; i < population.size(); i++) {	//loops through the population

				double currentValue = OptimisationFunction.unknownFunction(population.get(i));	//finds the output of the current gene

				if (currentValue < lowest) {	//if the current gene output is lower than the previous lowest
					lowest = currentValue;	//replace it
					bestGene = i;	//save the index of the best gene
				}
			}

			survivors.add(population.get(bestGene));	//adds the best gene to the top percent pool
			population.remove(bestGene); //remove the best gene from the population

			//repeats until the top popPercentage% of the population have been found
		}

		return survivors;
	}

	public static ArrayList<double[]> select (ArrayList<double[]> population) {

		double largest = Integer.MIN_VALUE;
		double smallest = Integer.MAX_VALUE;

		for (int i = 0; i < population.size(); i++) {

			double result = OptimisationFunction.unknownFunction(population.get(i));

			if (result > largest) {
				//finds the largest result in the population
				largest = result;
			}
			if (result < smallest) {
				//finds the smallest result in the population
				smallest = result;
			}
		}

		double range = largest-smallest;	//finds the difference between the largest and smallest result
		double twentyPercent = range * 0.2;	//finds 20% of the range
		double bottomPercent = smallest + twentyPercent;	//finds the number 20% from the bottom

		ArrayList<double[]> survivors = new ArrayList<double[]>();
		double rand = 0;
		double result = 0;

		for (int i = 0; i < population.size(); i++) {	//loop through the population

			double[] current = population.get(i);
			rand = Math.random();	//generates a random number between 0 and 1
			result = OptimisationFunction.unknownFunction(current);

			if (result < bottomPercent) {	//if the current result is within the best 20%
				if (rand > 0.2) {
					survivors.add(current);	//the gene has a 80% chance of being added to the survivor pool
				}
			}

			else {	//else if the current result is outside of the best 20%
				if (rand > 0.8) {
					survivors.add(current);	//the gene has a 20% chance of being added to the survivor pool
				}
			}
		}

		return survivors;

	}

	public static ArrayList<double[]> generatePopulation () {

		ArrayList<double[]> population = new ArrayList<double[]>(); //creates the empty population

		for (int pop = 0; pop < popSize; pop++) {
			//creates a new gene
			double[] p = new double[5];

			for (int gene = 0; gene < p.length; gene++) {
				//generates random numbers for the gene
				double g = -10 + Math.random() * 20;
				p[gene] = g;
			}

			population.add(p); //adds the individual to the population

			//			System.out.println("Individual added - " + printArray(p));

		}

		return population;
	}

	public static String printArray (double[] d) {

		String s = "";

		for (int i = 0; i < d.length-1; i++) {
			s += d[i] + ", ";
		}

		s += d[d.length-1];

		return s;
	}

	public static ArrayList<double[]> increaseVariance (ArrayList<double[]> population) {

		for (int i = 0; i < 1; i++) {
			//creates a new gene
			double[] p = new double[5];

			for (int gene = 0; gene < p.length; gene++) {
				//generates random numbers for the gene
				double g = -10 + Math.random() * 20;
				p[gene] = g;
			}

			population.add(p); //adds the individual to the population
		}

		return population;
	}

	public static void main(String[] args) {

		ArrayList<Double> repLows = new ArrayList<Double>();
		int timeSinceChange = 0;
		double previousBest = 0;

		for (int replicate = 0; replicate < 3; replicate++) {

			timeSinceChange = 0;

			double lowest = Integer.MAX_VALUE;	//finds the initial lowest value
			double[] lowestValues = new double[5];
			double genLowest = Integer.MAX_VALUE;
			//			double[] genLowestValues = new double[5];

			ArrayList<double[]> population = generatePopulation();

			System.out.println("Population generated.");

			for (int generation = 0; generation < numGenerations; generation++) {

				generationCount = generation;

				//				ArrayList<double[]> topPercent = getTopPercent(population); //finds the best performing individuals in the population

				ArrayList<double[]> survivors = select (population);

				population.clear(); //empty the population array, ready for the next generation
				//				population.addAll((ArrayList<double[]>)survivors.clone());	//adds the parents back into the population (should mean pop can't get worse)

				for (int i = 0; i < (popSize/2); i++) {
					//select two individuals from the survivors
					int firstSelection = (int) (1 + Math.random() * survivors.size()-1);
					int secondSelection = (int) (1 + Math.random() * survivors.size()-1);

					//if the two are the same, reselects
					while (firstSelection == secondSelection) {
						secondSelection = (int) (1 + Math.random() * survivors.size()-1);				
					} 

					//mutates and recombines the two individuals
					ArrayList<double[]> tempArray = basicRecombination(survivors.get(firstSelection), survivors.get(secondSelection));
					population.addAll(tempArray);
				}

				//				for (int i = 0; i < population.size(); i++) {
				//					population.set(i, mutate(population.get(i)));
				//				}

				genLowest = OptimisationFunction.unknownFunction(population.get(0));

				for (int i = 0; i < population.size(); i++) {
					double result = OptimisationFunction.unknownFunction(population.get(i));

					if (result < genLowest) {	//if the result is lower than the previous lowest for this generation
						genLowest = result;	//it becomes the new lowest
					}

					if (result < lowest) {	//if the result is lower than the previous lowest result overall
						lowest = result;	//it becomes the new lowest
						lowestValues = population.get(i);
						timeSinceChange = 0;	//if the overall lowest result is changed, reset the counter
					}
					
				}	//END OF POPULATION LOOP
				
				if (lowest == previousBest) {
					timeSinceChange++;
				}

				System.out.println("The lowest result from generation " + (generation+1) + " is: " + genLowest + ".");
//				System.out.println(timeSinceChange);
				System.out.println("The lowest result from all generations so far is " + lowest);
				//				System.out.println("The gene was {" + printArray(lowestValues) + "}");
				System.out.println("");

				if (n > 2) {
					n-=2;	//decrease the mutation factor
				}
				else {
					n*=2;
				}

				if (timeSinceChange > 25) {	//if there has been 20 generations with no change
					population = increaseVariance (population);	//introduce new variants into the population
					n *=2; //doubles the mutation factor
					System.out.println("New genes added.");
					timeSinceChange = 0;	//reset time since change
				}
				
				previousBest = lowest;

			}//END OF GENERATION

			for (int i = 0; i < population.size(); i++) {
				double result = OptimisationFunction.unknownFunction(population.get(i));
				if (result < lowest) {
					lowest = result;
					lowestValues = population.get(i);
				}
			}

			repLows.add(lowest);

			System.out.println("----------------------------------------------------------------------------------------------");
			System.out.println("");
			System.out.println("The lowest result overall for replicate " + (replicate+1) + " is: " + lowest + ", ");
			System.out.println("from the gene - {" + printArray(lowestValues) + "}.");
			System.out.println("");
			System.out.println("----------------------------------------------------------------------------------------------");

		} //END OF REPLICANT

		System.out.println("----------------------------------------------------------------------------------------------");
		System.out.println("");
		for (int i = 0; i < repLows.size(); i++) {
			System.out.println("The lowest result from replicate " + i + " was - " + repLows.get(i) );
		}
		System.out.println("");
		System.out.println("----------------------------------------------------------------------------------------------");
	}
}
