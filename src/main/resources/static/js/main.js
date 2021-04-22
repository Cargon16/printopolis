$(document).ready(function () {

  //Modal para añadir diseño
  var modal = document.getElementById("addDesignModal");

  // Get the button that opens the modal
  var btn = document.getElementById("boton_design");

  // Get the <span> element that closes the modal
  var span = document.getElementsByClassName("close")[0];

  // When the user clicks the button, open the modal
  if (btn != null)
    btn.onclick = function () {
      modal.style.display = "block";
    }

  // When the user clicks on <span> (x), close the modal
  if (span != null)
    span.onclick = function () {
      modal.style.display = "none";
    }

  // When the user clicks anywhere outside of the modal, close it
  window.onclick = function (event) {
    if (event.target == modal) {
      modal.style.display = "none";
    }
  }

  //Modal para añadir impresora
  var modalP = document.getElementById("addPrinterModal");

  // Get the button that opens the modal
  var btn1 = document.getElementById("boton_printer");

  // Get the <span> element that closes the modal
  var span = document.getElementsByClassName("close1")[0];

  // When the user clicks the button, open the modal
  if (btn1 != null)
    btn1.onclick = function () {
      modalP.style.display = "block";
    }

  // When the user clicks on <span> (x), close the modal
  if (span != null)
    span.onclick = function () {
      modalP.style.display = "none";
    }

  // When the user clicks anywhere outside of the modal, close it
  window.onclick = function (event) {
    if (event.target == modalP) {
      modalP.style.display = "none";
    }
  }

  jQuery('.btn[href^="#' + '"]').click(function (e) {
    e.preventDefault();
    var href = jQuery(this).attr('href');
    jQuery(href).modal('toggle');
  });

  var uploadField = document.getElementById("modelo3D");

  if(uploadField != null)
  uploadField.onchange = function () {
    if (this.files[0].size > 1048576) {
      alert("El archivo es demasiado grande");
      this.value = "";
    };
  };
});


document.addEventListener("DOMContentLoaded", () => {
  // selector para elegir sobre qué elementos validar
  let u = document.querySelectorAll('#email')[0]
  // cada vez que cambien, los revalidamos
  u.oninput = u.onchange =
  async () => u.setCustomValidity( // si "", válido; si no, inválido
    await validaUsername(u.value))
  })
  
  function go(url, method, data = {}) {
    let params = {
    method: method, // POST, GET, POST, PUT, DELETE, etc.
    headers: {
    "Content-Type": "application/json; charset=utf-8",
    },
    body: JSON.stringify(data)
    };
    if (method === "GET") {
    // GET requests cannot have body
    delete params.body;
    }
    console.log("sending", url, params)
    return fetch(url, params).then(response => {
    if (response.ok) {
      response.json().then((d) => {
        if(d.name == "")
          return data = "";
        else
          return data = d;
      });
      /*if(response.statusText == "OK")
        return data = "";
        else
          return data = response.json();*/
    } else {
    response.text().then(t => {throw new Error(t + ", at " + url)});
    }
    })
    }

  function validaUsername(username) {
    let params = {username: username};
    // Spring Security lo añade en formularios html, pero no en Ajax
    params[config.csrf.name] = config.csrf.value;
    // petición en sí
    var a = go(config.rootUrl + "user/username?id=" + username , 'GET', params)
    if(a == "") return "";
    else return "Nombre de usuario inválido o duplicado";
    }