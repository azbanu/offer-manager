<strong>Offer Management Rest Service</strong></br>
This web service allows offers to be managed via REST </br></br>

<strong>Technology Stack</strong></br>
Spring Boot</br>
Embeded H2 </br>
Spring</br>
Maven</br>
Jersey</br>
Mockito</br></br>

<strong>Assumptions</strong></br>
Expiry date is in ISO Date Time format (2019-03-25T00:00:00.000) </br>
All domain fields are mandatory </br>
No i18n support </br>
Scheduler can run every 10 seconds without impacting performance.   </br> 
Format in JSON only </br></br>

<strong>End points</strong></br>
Service entry point  at http://localhost:8080/api/offer </br>
Request to list all offers: GET  http://localhost:8080/api/offer/list </br>
Respond with all available offers </br></br>
 
Request to list offers with given description: GET http://localhost:8080/api/offer/list/?description=description </br>
Respond with all offers matching description</br></br>
 
Request to add offer: POST http://localhost:8080/api/offer/ using JSON payload </br>
Respond with the created offer </br>
{
   "name": "offer",
   "description": "offer description",
   "price": "2",
   "currency": "GBP",
   "expiryDate": "2019-03-04T20:49:02.231"
 }
</br></br>
  
Request to cancel offer: PUT http://localhost:8080/api/offer/cancel/{id} </br>
Respond with the cancelled offer </br></br>
  
Request to get offer by id: GET http://localhost:8080/api/offer/{id} </br>
Respond with the offer</br></br>

<strong>Build and Run</strong></br>
Java 8 and Maven 3.3.9</br>
Git clone https://github.com/azbanu/offer-manager.git </br>
cd to offer-manager </br>
mvn clean install </br>
java -jar target/offers-manager-1.0.0.jar </br>

