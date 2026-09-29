it worked in vscode with debugmcp extension. 
i installed java + spring boot extensions.(with full java enabled)
also added a launch.json etc
NOTE: the chat many-times was stuck waiting for something. So i had to manually kill the prompt execution. 
not as smooth as python debugging. 
Add the /debug-live skill from ~/.copilot

The following prompt worked:
/debug-live using  configuration "Java: ProductNpeDemoApplication" available in launch.json
Result: very fast, no latency.

- In case its stuck, it means it hasn't hit the breakpoint. So Manually do the curl request(make it hit some faulty API) while its waiting for the debugging status. Then it starts appearing in vscode. This is still kind-of gotcha but it works.
