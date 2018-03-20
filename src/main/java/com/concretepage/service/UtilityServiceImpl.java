package com.concretepage.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;

import com.concretepage.entity.Plans;
import com.concretepage.entity.SLCSP;
import com.concretepage.entity.Zips;

@Service
public class UtilityServiceImpl implements UtilityService {

	final Pattern pattern = Pattern.compile(",");

	// @Value("#{env[@environment+'.file.directory.contextRoot']}") -
	// can add a resource file and define the values
	private String importFileLocation = "src/resources";

	@SuppressWarnings("null")
	public void processSLCSP() {

		File f = new File(importFileLocation);
		File[] listOfFiles = f.listFiles();
		List<Zips> zips = new ArrayList<Zips>();
		List<Plans> plans = new ArrayList<Plans>();
		List<SLCSP> slcsp = new ArrayList<SLCSP>();

		if (null != listOfFiles && listOfFiles.length > 0) {
			try (Stream<Path> filePathStream = Files.walk(Paths.get(importFileLocation))) {
				System.out.println("Starting the process to stream through files at location: " + importFileLocation);
				filePathStream.forEach(filePath -> {
					if (Files.isRegularFile(filePath)) {
						String extension = FilenameUtils.getExtension(filePath.toString());
						String fileName = FilenameUtils.getName(filePath.toString());
						//restricting the import for CSV files only
						if (extension.equals("csv")) {
							if (fileName.contains("zips")) {
								zips.addAll(this.processZips(filePath.toString()));
							} else if (fileName.contains("plans")) {
								plans.addAll(processPlans(filePath.toString()));
							} else if (fileName.contains("slcsp")) {
								slcsp.addAll(processSLCSP(filePath.toString()));
							}
						}
					}
				});

				this.determineSLCSPAndWrite(zips, plans, slcsp);
				
			} catch (IOException e1) {
				System.out.println("Process File Data: There was an exception reading the files located at :"
						+ importFileLocation + " IOexception is: " + e1);

			}
		} else {
			System.out.println("Process File Data: No Files exists at " + importFileLocation);
		}

	}

	private void determineSLCSPAndWrite(List<Zips> zips, List<Plans> plans, List<SLCSP> slcsp) {
		Date date = new Date();
		//creating dynamic filename so we can dump unique files
		String outputFileLocation = "src/resources/output" + date.getTime() + ".csv";
		
		try {
			FileWriter writer = new FileWriter(outputFileLocation);
			//add header to the output File
			writer.append("Zipcode, Second Lowest Rate \n");

			slcsp.stream().forEach(sZips -> {
				Map<String, Zips> zipsMap = new HashMap<String, Zips>();
				//Stream through the Zips date set to grab just the matching values for a give zipcode
				List<Zips> filteredZips = zips.stream()
						.filter(zip -> zip.getZipcode().compareTo(sZips.getZipcode()) == 0)
						.collect(Collectors.toList());

				if (filteredZips.size() == 0) {
					System.out.println("There are no matching Zips for this ZipCode: " + sZips.getZipcode());
				} else if (filteredZips.size() == 1) {
					List<Plans> filteredPlans = plans.stream()
							.filter(plan -> plan.getState().compareToIgnoreCase(filteredZips.get(0).getState()) == 0
							&& plan.getRateArea().equals(filteredZips.get(0).getRateArea()))
							.collect(Collectors.toList());

					//Get all matching plans, if we have more than 2 then we can determing the SLCSP, if not its ambiguous
					if (filteredPlans.size() >= 2) {
						if (filteredPlans.get(0) != filteredPlans.get(1)) {
							// this should be second lowest
							try {
								writer.append(sZips.getZipcode() + "," + filteredPlans.get(1).getRate() + "\n");
							} catch (IOException e) {
								System.out.println("IOException encountered: " + e);
							}
						}
					} else {
						//writer.append(sZips.getZipcode() + "," + "\n"); //if we need to just insert zipcodes when no SLCSP
						System.out.println("Cannot determine second lowest for zipCode " + sZips.getZipcode());
					}

				} else {
					filteredZips.stream().forEach(zipI -> {
						if (zipsMap.get(zipI.getZipcode().toString() + zipI.getRateArea()) == null) {
							zipsMap.put(zipI.getZipcode().toString() + zipI.getRateArea(), zipI);
							List<Plans> zipfilteredPlans = plans.stream()
									.filter(plan -> plan.getState().compareToIgnoreCase(zipI.getState()) == 0
									&& plan.getRateArea().equals(zipI.getRateArea()))
									.collect(Collectors.toList());
							//This can be enhanced to get SLCSP based off a zipcode for all areas, i was not sure if this was part of the requirement but can be enhanced
						}

					});

				}
			});
			writer.flush();
			writer.close();
			System.out.println("Processing Complete, please check the SLCSP output file at:" + outputFileLocation);
		}

		catch (IOException ex) {
			System.out.println("IOException encountered: " + ex);
		}

	}

	@SuppressWarnings("null")
	private List<Zips> processZips(String string) {
		List<Zips> zips = new ArrayList<Zips>();

		try (BufferedReader in = new BufferedReader(new FileReader(string));) {

			List<Zips> allZips = in.lines().skip(1).map(line -> {
				String[] x = pattern.split(line);
				return new Zips(Long.parseLong(x[0]), x[1], Integer.parseInt(x[2]), x[3], Integer.parseInt(x[4]));
			}).collect(Collectors.toList());

			zips.addAll(allZips);
		} catch (FileNotFoundException e) {
			System.out.println("FileNotFoundException encountered: " + e);
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IOException encountered: " + e);

		}
		return zips;

	}

	private List<Plans> processPlans(String string) {
		List<Plans> plans = new ArrayList<Plans>();
		try (BufferedReader in = new BufferedReader(new FileReader(string));) {
			plans = in.lines().skip(1).map(line -> {
				String[] x = pattern.split(line);
				return new Plans(x[0], x[1], x[2], Float.parseFloat(x[3]), Integer.parseInt(x[4]));
			}).collect(Collectors.toList());

			plans = plans.stream().filter(plan -> "Silver".equalsIgnoreCase(plan.getmetalLevel()))
					.sorted((p1, p2) -> p1.getRate().compareTo(p2.getRate())).collect(Collectors.toList());
			return plans;
		} catch (FileNotFoundException e) {
			System.out.println("FileNotFoundException encountered: " + e);
		} catch (IOException e) {
			System.out.println("IOException encountered: " + e);

		}
		return plans;

	}

	private List<SLCSP> processSLCSP(String string) {
		List<SLCSP> slcsp = new ArrayList<SLCSP>();
		try (BufferedReader in = new BufferedReader(new FileReader(string));) {
			slcsp = in.lines().skip(1).map(line -> {
				String[] x = pattern.split(line);
				return new SLCSP(Long.parseLong(x[0]), Integer.parseInt(x[1]));
			}).collect(Collectors.toList());
			return slcsp;

		} catch (FileNotFoundException e) {
			System.out.println("FileNotFoundException encountered: " + e);
		} catch (IOException e) {
			System.out.println("IOException encountered: " + e);
		}
		return slcsp;

	}

}
