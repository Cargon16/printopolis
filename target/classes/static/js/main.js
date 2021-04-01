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
  if(span != null)
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
  if(span != null)
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
});

