# akka_cqrs

**To Run the cluster**

 Open a Terminal Run 
   
 ```sbt -DHTTP_PORT=8081 -DCLUSTER_PORT=2554 -DUSER_MANAGEMENT_BIND_PORT=9091 -DNODE_HOSTNAME={YOUR_HOST} -DPERSISTENT_ENTITY_TIMEOUT=10s  "runMain com.navneetgupta.usermanagement.Main"``` 
 
 Open Another Terminal Run  
   
 ```sbt -DHTTP_PORT=8080 -DCLUSTER_PORT=2553 -DUSER_MANAGEMENT_BIND_PORT=9090 -DNODE_HOSTNAME={YOUR_HOST} -DPERSISTENT_ENTITY_TIMEOUT=10s  "runMain com.navneetgupta.usermanagement.Main"```
