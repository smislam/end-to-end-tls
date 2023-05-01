import * as cdk from 'aws-cdk-lib';
import { Certificate } from 'aws-cdk-lib/aws-certificatemanager';
import { Peer, Port, Vpc } from 'aws-cdk-lib/aws-ec2';
import { Cluster, ContainerImage, FargateService, FargateTaskDefinition, LogDrivers } from 'aws-cdk-lib/aws-ecs';
import { ApplicationLoadBalancer, ApplicationProtocol, SslPolicy } from 'aws-cdk-lib/aws-elasticloadbalancingv2';
import { StringParameter } from 'aws-cdk-lib/aws-ssm';
import { Construct } from 'constructs';

export class EndToEndTlsStack extends cdk.Stack {
  constructor(scope: Construct, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    const vpc = new Vpc(this, 'app-vpc', {});

    const cluster = new Cluster(this, 'Cluster', {vpc});

    const taskDefinition = new FargateTaskDefinition(this, 'TaskDef', {});

    const container = taskDefinition.addContainer('myContainer', {
      image: ContainerImage.fromAsset('./bcfips'),
      memoryLimitMiB: 256,
      cpu: 256,
      portMappings: [{
        containerPort: 9999,
        hostPort: 9999
      }],
      logging: LogDrivers.awsLogs({streamPrefix: 'my-tls-service'}),
    });

    // Instantiate an Amazon ECS Service
    const ecsService = new FargateService(this, 'Service', {
      cluster,
      taskDefinition,
      desiredCount: 1
    });

    const alb = new ApplicationLoadBalancer(this, 'alb', {
      vpc,
      internetFacing: true
    });

    const cert = Certificate.fromCertificateArn(this, 'albcert', StringParameter.valueForStringParameter(this, 'cert-arn'));


    const listener = alb.addListener('e2e-listener', {
      port: 443,
      protocol: ApplicationProtocol.HTTPS,
      certificates: [cert],
      sslPolicy: SslPolicy.FORWARD_SECRECY_TLS12
    });

    listener.addTargets('e2e-target', {
      port: 9999,
      targets: [ecsService],
      protocol: ApplicationProtocol.HTTPS,
      healthCheck: {
        healthyThresholdCount: 2,
        unhealthyThresholdCount: 10,
        timeout: cdk.Duration.seconds(20),
        interval: cdk.Duration.seconds(30)
      }
    });

    new cdk.CfnOutput(this, 'alb-url', {
      value: alb.loadBalancerDnsName,
      exportName: 'e2e-tls-stack-loadBalancerDnsName'
    });

    
  }
}
