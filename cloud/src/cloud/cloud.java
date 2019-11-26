package cloud;

import java.util.Scanner;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;

import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;


import com.amazonaws.services.ec2.model.DryRunResult;
import com.amazonaws.services.ec2.model.DryRunSupportedRequest;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesRequest;

import com.amazonaws.services.ec2.model.DescribeRegionsResult;
import com.amazonaws.services.ec2.model.Region;
import com.amazonaws.services.ec2.model.AvailabilityZone;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesResult;

import com.amazonaws.services.ec2.model.InstanceType;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;


import com.amazonaws.services.ec2.model.RebootInstancesRequest;
import com.amazonaws.services.ec2.model.RebootInstancesResult;

import com.amazonaws.services.ec2.model.Image;
import com.amazonaws.services.ec2.model.DescribeImagesRequest;
import com.amazonaws.services.ec2.model.DescribeImagesResult;

import com.amazonaws.services.ec2.model.TerminateInstancesRequest;

public class cloud {

	static AmazonEC2 ec2;

	private static void init() throws Exception {

		ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider();
		try {
			credentialsProvider.getCredentials();
		} catch (Exception e) {
			throw new AmazonClientException("Cannot load the credentials from the credential profiles file. "
					+ "Please make sure that your credentials file is at the correct "
					+ "location (~/.aws/credentials), and is in valid format.", e);

		}
		ec2 = AmazonEC2ClientBuilder.standard().withCredentials(credentialsProvider).withRegion("us-east-1").build();
	}

	public static void main(String[] args) throws Exception {
		init();
		Scanner menu = new Scanner(System.in);
		Scanner id_string = new Scanner(System.in);

		int number = 0;
		String instance_id;

		while (true) {
			
			System.out.println(" ");
			System.out.println(" ");
			System.out.println("------------------------------------------------------------");
			System.out.println(" Amazon AWS Control Panel using SDK ");
			System.out.println(" ");
			System.out.println(" Cloud Computing, Computer Science Department ");
			System.out.println(" at Chungbuk National University ");
			System.out.println("------------------------------------------------------------");
			System.out.println(" 1. list instance 2. available zones ");
			System.out.println(" 3. start instance 4. available regions ");
			System.out.println(" 5. stop instance 6. create instance ");
			System.out.println(" 7. reboot instance 8. list images ");
			System.out.println(" 9. Delete instance 10. Exit ");
			System.out.println("------------------------------------------------------------");
			System.out.print("Enter an integer:");

			number = menu.nextInt();

			switch (number) {
			case 1:
				listInstances();
				break;
			case 2:
				Describezone();
				break;
			case 3:
				listInstances();
				System.out.print("\n Enter your Id : ");
				instance_id=id_string.nextLine();
				startInstance("i-0254f76d95970ca40");
				break;
			case 4:
				Describeregion();
				break;
			case 5:
				listInstances();
				System.out.print("\n Enter your Id : ");
				instance_id=id_string.nextLine();
				stopInstance(instance_id);
				break;
			case 6:
				CreateInstance();
				break;
			case 7:
				listInstances();
				System.out.print("\n Enter your Id : ");
				instance_id=id_string.nextLine();
				RebootInstance(instance_id);
			case 8:
				ListImages();
				break;
			case 9:
				listInstances();
				System.out.print("\n Enter your Id : ");
				instance_id=id_string.nextLine();
				DeleteInstance(instance_id);
				break;
			case 10:
				System.out.println("The program will now exit!\n"
						+ "Bye Bye");
				System.exit(0);
				break;	
			}
		}
	}

	// #1 show instance list
	public static void listInstances() {
		System.out.println("Listing instances....");
		boolean done = false;

		DescribeInstancesRequest request = new DescribeInstancesRequest();
		while (!done) {
			DescribeInstancesResult response = ec2.describeInstances(request);
			for (Reservation reservation : response.getReservations()) {
				for (Instance instance : reservation.getInstances()) {
					System.out.printf(
							"[id] %s, " + "[AMI] %s, " + "[type] %s, " + "[state] %10s, " + "[monitoring state] %s",
							instance.getInstanceId(), instance.getImageId(), instance.getInstanceType(),
							instance.getState().getName(), instance.getMonitoring().getState());
				}
				System.out.println();
			}
			request.setNextToken(response.getNextToken());

			if (response.getNextToken() == null) {
				done = true;
			}
		}
	}
	public static void Describezone() {
    DescribeAvailabilityZonesResult zones_response = ec2.describeAvailabilityZones();

        for(AvailabilityZone zone : zones_response.getAvailabilityZones()) {
            System.out.printf("Found availability zone %s " 
            		+ "with status %s " + "in region %s \n", 
            		zone.getZoneName(), zone.getState(), zone.getRegionName());
        }
	}
	
	public static void startInstance(String instance_id)
    {

        DryRunSupportedRequest<StartInstancesRequest> dry_request =
            () -> {
            StartInstancesRequest request = new StartInstancesRequest()
                .withInstanceIds(instance_id);

            return request.getDryRunRequest();
        };

        DryRunResult dry_response = ec2.dryRun(dry_request);

        if(!dry_response.isSuccessful()) {
            System.out.printf("Failed dry run to start instance %s", instance_id);

            throw dry_response.getDryRunResponse();
        }

        StartInstancesRequest request = new StartInstancesRequest()
            .withInstanceIds(instance_id);

        ec2.startInstances(request);

        System.out.printf("Successfully started instance %s", instance_id);
    }

	public static void Describeregion() 
	{
		
		DescribeRegionsResult regions_response = ec2.describeRegions();

		for(Region region : regions_response.getRegions()) 
		{
			System.out.printf("Found region %s " + "with endpoint %s \n",
					region.getRegionName(),region.getEndpoint());
		}
		

	}
public static void stopInstance(String instance_id)
{


    DryRunSupportedRequest<StopInstancesRequest> dry_request =
        () -> {
        StopInstancesRequest request = new StopInstancesRequest()
            .withInstanceIds(instance_id);

        return request.getDryRunRequest();
    };

    DryRunResult dry_response = ec2.dryRun(dry_request);

    if(!dry_response.isSuccessful()) {
        System.out.printf(
            "Failed dry run to stop instance %s", instance_id);
        throw dry_response.getDryRunResponse();
    }

    StopInstancesRequest request = new StopInstancesRequest()
        .withInstanceIds(instance_id);

    ec2.stopInstances(request);

    System.out.printf("Successfully stop instance %s", instance_id);
}
public static void CreateInstance()
{
    final String USAGE ="To run this example, supply an instance name and AMI image id\n" 
    				+ "Ex: CreateInstance <instance-name> <ami-image-id>\n";

    String ami_id = "ami-0129016fca4afc565";


    RunInstancesRequest run_request = new RunInstancesRequest()
        .withImageId(ami_id)
        .withInstanceType(InstanceType.T2Micro)
        .withMaxCount(1)
        .withMinCount(1);

    RunInstancesResult run_response = ec2.runInstances(run_request);

    String reservation_id = run_response.getReservation().getInstances().get(0).getInstanceId();

    System.out.printf("Successfully started EC2 instance %s based on AMI %s", reservation_id, ami_id);
}
public static void RebootInstance(String instance_id)
{
	if(instance_id.length()==0) {
		System.out.println("no instance ");
		System.exit(1);
	}
   
    RebootInstancesRequest request = new RebootInstancesRequest()
        .withInstanceIds(instance_id);

    RebootInstancesResult response = ec2.rebootInstances(request);

    System.out.printf(
        "Successfully rebooted instance %s", instance_id);
}

public static void ListImages() {

	System.out.println("Listing AMI....");

	DescribeImagesRequest request = new DescribeImagesRequest().withOwners("self");

	DescribeImagesResult iresponse = ec2.describeImages(request);

	for (Image I : iresponse.getImages()) {
		System.out.printf("[AMIid] %s, [Owner/Name] %s \n", I.getImageId(), 
				I.getImageLocation(), I.getName());
	}

}

public static void DeleteInstance(String instance_id) {
	
	
	String delete_in = instance_id;
	
	TerminateInstancesRequest ter = new TerminateInstancesRequest().withInstanceIds(delete_in);
	ec2.terminateInstances(ter);
	System.out.printf("Successfully deleted EC2 instance %s", instance_id);
}
}
