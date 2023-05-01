#!/usr/bin/env node
import 'source-map-support/register';
import * as cdk from 'aws-cdk-lib';
import { EndToEndTlsStack } from '../lib/end-to-end-tls-stack';

const app = new cdk.App();
new EndToEndTlsStack(app, 'EndToEndTlsStack', {
  env: { account: process.env.CDK_DEFAULT_ACCOUNT, region: process.env.CDK_DEFAULT_REGION }
});