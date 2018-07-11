Please follow the below steps for running the project:

Pre steps for installation:
1. Install cassandra:
Refer to this link for installing cassandra in mac:
https://www.datastax.com/2012/01/working-with-apache-cassandra-on-mac-os-x

Purpose: For storing the car details, each transaction details.

2. Install Redis:
Refer to this link for installing redis in mac:
https://medium.com/@petehouston/install-and-config-redis-on-mac-os-x-via-homebrew-eb8df9a4f298

Purpose: For storing count of car for each model to handle parallel transaction.

3. Create tables in cassandra:
a)
CREATE KEYSPACE bytewheel WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'}  AND durable_writes = true;

Usecase: A Keyspace for creating the required tables


b)
CREATE TABLE bytewheel.car_details (
category text,
car_id text,
car_name text,
cost_per_day int,
max_car_avail int,
PRIMARY KEY (category, car_id))
WITH CLUSTERING ORDER BY (car_id ASC);

Usecase: Static table for storing details of car like types of car, cost of car, number of each type of car etc.


c)
CREATE TABLE bytewheel.user_details (
upin text PRIMARY KEY,
email text,
location text,
phone_number text
);

Usecase: Table to store details of customers like email id, phone number etc.


d)
CREATE TABLE bytewheel.user_tracking (
upin text,
date date,
car_id text,
last_updated timestamp,
number_of_cars int,
status text,
total_cost double,
PRIMARY KEY (upin, date, car_id, last_updated))
WITH CLUSTERING ORDER BY (date ASC, car_id ASC, last_updated DESC);
CREATE INDEX user_tracking_date_idx ON bytewheel.user_tracking (date);

Usecase: Table to store details of each transaction like user name, pickup date, car type, number of cars, cost etc.

4. Insert queries: using dummy data
a) Insert queries for bytewheel.car_details:
insert into bytewheel.car_details (category , car_id , car_name , max_car_avail , cost_per_day ) VALUES ( 'COMPACT', 'ford_focus', 'Ford Focus', 2 , 20);
insert into bytewheel.car_details (category , car_id , car_name , max_car_avail , cost_per_day ) VALUES ( 'COMPACT', 'ford_fiesta', 'Ford Fiesta', 2 , 20);
insert into bytewheel.car_details (category , car_id , car_name , max_car_avail , cost_per_day ) VALUES ( 'LUXURY', 'bmw_x5', 'BMW X5', 2 , 50);
insert into bytewheel.car_details (category , car_id , car_name , max_car_avail , cost_per_day ) VALUES ( 'LUXURY', 'bmw_328i', 'BMW 328i', 2 , 50);
insert into bytewheel.car_details (category , car_id , car_name , max_car_avail , cost_per_day ) VALUES ( 'LARGE', 'ford_escape', 'Ford Escape', 2 , 40);
insert into bytewheel.car_details (category , car_id , car_name , max_car_avail , cost_per_day ) VALUES ( 'LARGE', 'ford_egde', 'Ford Egde', 2 , 40);
insert into bytewheel.car_details (category , car_id , car_name , max_car_avail , cost_per_day ) VALUES ( 'FULL', 'volkswagen_jetta', 'Volkswagen Jetta', 2 , 30);
insert into bytewheel.car_details (category , car_id , car_name , max_car_avail , cost_per_day ) VALUES ( 'FULL', 'chevrolet_malibu', 'Chevrolet Malibu', 2 , 30);

b) Insert queries for bytewheel.user_details:
insert into bytewheel.user_details (upin , email , location , phone_number ) VALUES ( 'TEST_UPIN', 'nitiprabhu@gmail.com', 'test_location', '1234' );

c) Insert queries for bytewheel.user_tracking:
insert into bytewheel.user_tracking (upin , date , car_id , last_updated , number_of_cars , status , total_cost ) VALUES ( 'TEST_UPIN', '2016-01-01', 'ford_fiesta', 1, 'Booked', '50');


6) Copy the below mentioned jar and run it using java with run parameters as :


java -Dcassandra.address= -Dcassandra.port= -Dspring.mail.username= -Dspring.mail.password= -jar jar_file.java

jar_file.java Link - https://github.com/nitiprabhu/rentalCarService/blob/master/carrent-0.0.1-SNAPSHOT.jar


API Instructions: 

#Please import CarRent.postman_collection file to test through postman

1. When a user picks the date for car rent, client will send a request to backend as follows.

Controller URL - /pickupdate
Method - POST
Headers -Content-Type:application/json
Body - {"upin":"upin", "requestDate":"requestDate"}

Response - {
    "categories": [],
    "requestedDate": "requestedDate",
    "upin": "upin"
}

definition:

upin - unique id given to the user;
requestDate - date picked by the user for the car rent
categories - List of categories available for car rent

Example:

RequestBody - {"upin":"TEST_UPIN", "requestDate":"2019-01-01"}
ResponseBody - {"categories":["LUXURY","LARGE","FULL","COMPACT"],"requestedDate":"2019-01-01","upin":"TEST_UPIN"}


2. When a user opted categories, client will send a request as follows.

Controller URL - /optedCategories
Method - POST
Headers -Content-Type:application/json
Body - {"requestDate":"requestDate","upin":"upin","categoriesOpted":["category_list"]}

Response - {
    "requestDate": "requestDate",
    "carDetails": [{
            "category": "category",
            "carId": "carId",
            "carName": "carName",
            "maxCarAvail": maxCarAvail,
            "costPerDay": costPerDay
        }],
    "upin": "upin"
}

definition:

category - Category of the Car
carId - CarId of the Car
carName - Name of the car
maxCarAvail - Maximum available Car
costPerDay - Cost of the Car per day


Example:

RequestBody - {"requestDate":"2019-02-07","upin":"TEST_UPIN","categoriesOpted":["LUXURY","COMPACT"]}
Response - {
    "requestDate": "2019-02-07",
    "carDetails": [
        {
            "category": "LUXURY",
            "carId": "bmw_328i",
            "carName": "BMW 328i",
            "maxCarAvail": 2,
            "costPerDay": 50
        },
        {
            "category": "LUXURY",
            "carId": "bmw_x5",
            "carName": "BMW X5",
            "maxCarAvail": 2,
            "costPerDay": 50
        },
        {
            "category": "COMPACT",
            "carId": "ford_fiesta",
            "carName": "Ford Fiesta",
            "maxCarAvail": 0,
            "costPerDay": 20
        },
        {
            "category": "COMPACT",
            "carId": "ford_focus",
            "carName": "Ford Focus",
            "maxCarAvail": 2,
            "costPerDay": 20
        }
    ],
    "upin": "TEST_UPIN"
}

3. When a user book the car, client will send a request as follows.

Controller URL - /bookcar
Headers - Content-Type - application/json
Method - POST
Body - {
    "requestDate": "requestDate",
    "upin":"upin",
    "carBookedDetails": [
        {
            "category": "category",
            "carId": "carId",
            "numberOfCarBooked": numberOfCarBooked,
            "costPerDay": "costPerDay"
        }
    ]
}

Response - {
    "upin": "upin",
    "requestDate": "requestDate",
    "carBookedDetails": [
        {
            "category": "category",
            "carId": "carId",
            "numberOfCarBooked": numberOfCarBooked,
            "bookedSuccessful": isBookedSuccessful,
            "costPerDay": costPerDay
        }
    ]
}

Definition:

category - Category of the Car
carId - CarId of the Car
carName - Name of the car
bookedSuccessful - True if booked successully, false if not
costPerDay - Cost of the Car per day

Example:

RequestBody - {
    "requestDate": "2019-02-10",
    "upin":"TEST_UPIN",
    "carBookedDetails": [
        {
            "category": "COMPACT",
            "carId": "ford_fiesta",
            "numberOfCarBooked": 1,
            "costPerDay": "20"
        }
    ]
}

ResponseBody - {
    "upin": "TEST_UPIN",
    "requestDate": "2019-02-10",
    "carBookedDetails": [
        {
            "category": "COMPACT",
            "carId": "ford_fiesta",
            "numberOfCarBooked": 1,
            "bookedSuccessful": true,
            "costPerDay": 20
        }
    ]
}

4. When an admin picks date for collecting report, client will send a following request as follows.

Controller URL - /collectreport
Headers - Content-Type: multipart/form-data
Method - POST
Parameters - "name": "requestDate", "value": "requested_date"

Response - [
    {
        "upin": "upin",
        "date": {
            "year": year,
            "month": month,
            "chronology": {
                "id": "id",
                "calendarType": "calendarType"
            },
            "monthValue": monthValue,
            "dayOfMonth": dayOfMonth,
            "dayOfWeek": "dayOfWeek",
            "era": era,
            "dayOfYear": dayOfYear,
            "leapYear": leapYear
        },
        "carId": "carId",
        "lastUpdated": {
            "nano": nan,
            "epochSecond": ephoc_seconds
        },
        "numberOfCars": number of cars,
        "status": status,
        "totalCost": total cost
    }
]

definition:

numberOfCars - Number of car booked by the user on the date
totalCost - Total cost for car booked

Example:

Request - Content-Disposition: form-data; name="requestDate", value = 2019-02-09

Response - [
    {
        "upin": "TEST_UPIN",
        "date": {
            "year": 2019,
            "month": "FEBRUARY",
            "chronology": {
                "id": "ISO",
                "calendarType": "iso8601"
            },
            "monthValue": 2,
            "dayOfMonth": 9,
            "dayOfWeek": "SATURDAY",
            "era": "CE",
            "dayOfYear": 40,
            "leapYear": false
        },
        "carId": "ford_fiesta",
        "lastUpdated": {
            "nano": 474000000,
            "epochSecond": 1531319302
        },
        "numberOfCars": 1,
        "status": "SUCCESS",
        "totalCost": 20
    },
    {
        "upin": "TEST_UPIN",
        "date": {
            "year": 2019,
            "month": "FEBRUARY",
            "chronology": {
                "id": "ISO",
                "calendarType": "iso8601"
            },
            "monthValue": 2,
            "dayOfMonth": 9,
            "dayOfWeek": "SATURDAY",
            "era": "CE",
            "dayOfYear": 40,
            "leapYear": false
        },
        "carId": "ford_fiesta",
        "lastUpdated": {
            "nano": 378000000,
            "epochSecond": 1531319273
        },
        "numberOfCars": 1,
        "status": "SUCCESS",
        "totalCost": 20
    }
]
