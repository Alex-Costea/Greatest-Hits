import random
import math
import operator

male=None
female=None
last=None
titles=None
with open("male","r") as male_file:
    with open("female","r") as female_file:
        with open("last","r") as last_file:
            with open("titles","r") as titles_file:
                male=[x for x in male_file.read().split("\n") if x!=""]
                female=[x for x in female_file.read().split("\n") if x!=""]
                last=[x for x in last_file.read().split("\n") if x!=""]
                titles=list(set([x for x in titles_file.read().split("\n") if x!=""]))

def male_arist():
    firstname=random.choice(male)
    middlename=random.choice(male)
    lastname=random.choice(last)
    middle=random.randint(1,5)==5
    return (firstname+" "+(middlename+" " if middle else "")+lastname).title()

def female_arist():
    firstname=random.choice(female)
    middlename=random.choice(female)
    lastname=random.choice(last)
    middle=random.randint(1,5)==5
    return (firstname+" "+(middlename+" " if middle else "")+lastname).title()
                        
def either_arist():
    return male_arist() if random.randint(1,2)==2 else female_arist()

def song_title():
    return random.choice(titles)


const=(1-(math.atan(5)/(math.pi/2))**10)
def hypedecay(x):
    return (1-(math.atan(x+5)/(math.pi/2))**10)/const

def logistics(x):
    return 1/(1+math.e**(-x))

artists=[either_arist() for i in range(500)]

class song:
    def __init__(self,myid):
        self.name=random.choice(artists)+" -- "+song_title()
        self.decay=random.randint(90,96)*0.01
        self.popularity=(random.randint(0,90)/100)**4
        self.fan_reception=random.randint(100,1000)/100.0
        self.myid=myid
        self.week=0
        #self.add_week()
    def add_week(self):
        self.week+=1
        self.hype_randomizer=(10**(random.randint(40,160)/100))/(10**1.6)
        self.hype=hypedecay(self.week-1)*(self.hype_randomizer)/2
        self.popularity=1-(1-self.popularity)*(1-self.hype)
        self.fan_reception=self.fan_reception*(self.decay+random.randint(0,20)*0.001)
        self.points=(self.popularity*self.fan_reception*10)
        self.points=self.points**1.5/((100/self.points))

full_points=dict()

with open("charts","w") as charts_file:
    l=[song(i) for i in range(0,100)]
    for x in l:
            x.add_week()
    last_sorted=None
    idc=1000
    for i in range(153):
        week=i-100
        sorted_l=sorted(l,key=(lambda x:x.points),reverse=True)[:40]
        if week>0:
            charts_file.write("Week "+str(week)+"\n")
            for j,x in enumerate(sorted_l):
                name_id=x.name+"@"+str(x.myid)
                try:
                    last_pos=list(map(lambda y:y==x.myid,last_sorted)).index(True)
                except ValueError:
                    last_pos=-1
                if last_pos==-1:
                    if name_id in full_points:
                        mystr="(re-entry)"
                    else:
                        mystr="(new)"
                elif last_pos==j:
                    mystr="(=)"
                elif last_pos>j:
                    mystr="(+"+str(last_pos-j)+")"
                else:
                    mystr="("+str(last_pos-j)+")"
                charts_file.write("#"+str(j+1)+" "+x.name+" "+mystr+" "+str(int(x.points))+".\n")
                full_points[name_id]=full_points.get(name_id,0)+x.points
            charts_file.write("\n")
        l=[x for x in l if x.points>1]
        l=l+[song(idc+i) for i in range(0,20)]
        idc+=1000
        for x in l:
            x.add_week()
        if week>=0:
            last_sorted=[x.myid for x in sorted_l]
            
    charts_file.write("Year End\n")
    for i,y in enumerate(sorted(full_points.items(), key=operator.itemgetter(1),reverse=True)[:40]):
        charts_file.write("#"+str(i+1)+" "+y[0].split("@")[0]+"\n")
