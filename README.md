# Example of End to End Encryption with Spring Boot and AWS ALB

This example uses encrypted connection all the way to your ECS hosted application (Not Container) using TLS.  This is a requirement for Zero-Trust Architecture (https://www.nist.gov/publications/zero-trust-architecture).

It enables encrypted traffic between the browser and AWS ALB and then again from AWS ALB to your Spring boot application running in ECS container.  The ALB offloads the first TLS certificate.  A new TLS connection is created using the Certificate (FIPS Complaint) used in the Spring Boot application from ALB which then terminates into your application.

## Architecture
![image](e2e-tls-architecture.PNG "End to End Encryption with AWS ALB and Spring Boot")

## Implementation Details
This example uses:
* AWS ELB with TLS using AWS CDK
* Terminates TLS on ALB.  Uses TLS certificate already stored in AWS ACM for ALB.  I do not show this step.
* Creates a new TLS Connection and terminates TLS in Spring Boot application (does not terminate TLS in the container).
* Uses the bcfips Spring Boot project and deploys to ECS. https://github.com/smislam/bcfips 

## Steps to run
* Read more about end to end encryption:  https://docs.aws.amazon.com/AmazonECS/latest/bestpracticesguide/security-network.html

## Results
If all goes well, you should see this:
![image](e2e-encryption.PNG "End to End Encryption with AWS ALB and Spring Boot")

### Notes
* Oh, you will see the ALB certificate is not trusted in this example since I created a self-signed certificate and pushed to AWS ACM.  You should create a Route53 domain and add a validated certificate to ACM.