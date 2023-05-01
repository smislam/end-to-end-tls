# Use Bouncy Castle FIPS framework with Spring Boot

This example uses of bcfips to enable SSL on Spring boot application.  

Compliant with NIST FIPS 140-2 requirements.  You can read more about it here:

### BouncyCastle FIPS
https://www.bouncycastle.org/fips_faq.html

### FIPS 140-2
https://csrc.nist.gov/publications/detail/fips/140/2/final

## Steps to run
* Run `mvn package` to create the certs and keystores
* Run the app