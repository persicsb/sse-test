var reqListener = function() {
    var uid = this.responseText;
    var es = new EventSource("/hello/" + uid);
    es.onmessage = function(event) {
      if(!event) {
            es.close();
            console.log("Event Source Closed NOEVENT");
      } else {
        const newElement = document.createElement("li");
        const eventList = document.getElementById("content");

        newElement.innerHTML = "message: " + event.data;
        eventList.appendChild(newElement);
      }
    }

    es.onerror = function(event) {
        if (event.eventPhase == EventSource.CLOSED) {
            console.log("Event Source Closed ERROR");
            es.close();
        }
    }
}

var world = {name: "world"};

var xhr = new XMLHttpRequest();
xhr.addEventListener("load", reqListener);
xhr.open("POST", "/hello/");
xhr.setRequestHeader("Content-type", "application/json");
xhr.send(JSON.stringify(world));