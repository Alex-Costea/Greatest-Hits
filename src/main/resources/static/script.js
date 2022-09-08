var minWeek=0;
while(document.getElementById("div"+(minWeek+1))===null)
{
    minWeek++;
    break;
}

var maxWeek=minWeek;
while(document.getElementById("div"+(maxWeek+1))!==null)
{
    maxWeek++;
}
var week=maxWeek;
setElementHidden(false)
document.getElementById("previousButton").addEventListener("click", previous);
document.getElementById("nextButton").addEventListener("click", next);

function setElementHidden(hidden)
{
  p = document.getElementById("div"+week);
  p.style.display=hidden?"none":"";
}

function previous() {
  if(week>1)
  {
    setElementHidden(true);
    week--;
    setElementHidden(false);
  }
}

  function next() {
  if(week<maxWeek)
  {
    setElementHidden(true);
    week++;
    setElementHidden(false);
  }
}