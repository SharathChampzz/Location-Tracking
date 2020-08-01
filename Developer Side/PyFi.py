#Tutorials Followed : 
#1: https://morioh.com/p/a593f973aff0
#2: Firebase Documentaion

import firebase_admin
from firebase_admin import credentials
from firebase_admin import db
import json

cred = credentials.Certificate("covid-warriors-ed9ec-firebase-adminsdk-amphh-ecb271fd3d.json")

# Initialize the app with a service account, granting admin privileges
'''
firebase_admin.initialize_app(cred, {
    'databaseURL': 'https://covid-warriors-ed9ec.firebaseio.com/'
})
'''
ref = db.reference()
json_obj = ref.get()
#print(json_obj)

#print(json.dumps(json_obj, indent=4))
#print(json_obj)

import mysql.connector, os, json

# read JSON file which is in the next parent folder
#file = os.path.abspath(r'C:\Users\Basavanna hosur\Desktop') + "/rad.json"
#json_data=open(file).read()
#json_obj = json.loads(data)
#json=json.dumps(json_obj,indent=2)
#print(json)
# do validation and checks before insert
def validate_string(val):
   if val != None:
        if type(val) is int:
            #for x in val:
            #   print(x)
            return str(val).encode('utf-8')
        else:
            return val

# connect to MySQL
con = mysql.connector.connect(host = 'localhost',user = 'root',passwd = '',database = 'ex2')
cursor = con.cursor()
#json=json.dumps(json_obj,indent=2)
#json=list(json)

# parse json data to SQL insert
'''
#for i, item in enumerate(json_obj):
for state in json_obj.keys():
    states=json_obj[state]
    for dist in states.keys():
        districts=states[dist]
        for city in districts.keys():           
            cities=districts[city]
            for p_no in cities.keys():
                p_nos=cities[p_no]
                for key in p_nos.keys():
                    keys=p_nos[key]
                    for ele in keys.keys():
                        name = validate_string(keys["name"])
                        lattitude = validate_string(keys["latitude"])[0:6]
                        longitude = validate_string(keys["longitude"])[0:6]
                        day = validate_string(keys["day"])
                        time = validate_string(keys["time"])
                        cursor.execute("INSERT INTO info (state,dist,city,phone_no,name,lattitude,longitude,day,time) VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s )", (state,dist,city,p_no,name,lattitude,longitude,day,time))
    print(state,dist,city,p_no,key,name,lattitude,longitude,day,time)  
        
'''        
n=input("Enter phone number of covid patient\n")        
sql = "SELECT * from info WHERE phone_no={}".format(n)

try:
   # Execute the SQL command
   cursor.execute(sql)
   # Fetch all the rows in a list of lists.
   results = cursor.fetchall()
   for row in results:
      state = row[0]
      dist = row[1]
      city = row[2]
      phone_no = row[3]
      lat = row[5]
      long=row[6]
      day=row[7]
      time=row[8]
      # Now print fetched result
      print("PHONE_NO={} lat={} long={} day={} time={}" .format(phone_no,lat,long,day,time ))
except:
    print ("Error: unable to fecth data")
    


sql="SELECT phone_no FROM info WHERE lattitude={} AND longitude={}".format(lat,long)
cursor.execute(sql)
ls=list(cursor)
ls =set(ls)

print("\n\n\n\nPrimary contacts with positive person")
for i in ls:
    print(i)
con.commit()
con.close()

    
    
    
    
    
    
    
    
    
    
    
    
    

