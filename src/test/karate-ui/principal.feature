Feature: browser automation 1

Background:
  # chromium bajo linux; 
  # si usas google-chrome, puedes quitar toda la parte de executable
  * configure driver = { type: 'chrome', showDriverLog: true }
    
  # descarga geckodriver de https://github.com/mozilla/geckodriver/releases para probar bajo firefox
  # * configure driver = { type: 'geckodriver', executable: './geckodriver', showDriverLog: true }

  # drivers que no he probado
  # * configure driver = { type: 'chrome', showDriverLog: true }
  # * configure driverTarget = { docker: 'justinribeiro/chrome-headless', showDriverLog: true }
  # * configure driverTarget = { docker: 'ptrthomas/karate-chrome', showDriverLog: true }
  # * configure driver = { type: 'chromedriver', showDriverLog: true }
  # * configure driver = { type: 'safaridriver', showDriverLog: true }
  # * configure driver = { type: 'iedriver', showDriverLog: true, httpConfig: { readTimeout: 120000 } }
  
Scenario: try to login to printopolis
    and then upload a design

 Given driver 'http://localhost:8080/login'
  * input('#username', 'a')
  * input('#password', 'aa')
  * submit().click("input[id=signin]")
  * match html('title') contains 'Admin'
  
  * click("a[id=btnDesigns]")
  * waitFor('#hola').click()
  * match html('title') contains 'Printopolis'
  * driver.screenshot()
 
  # voy a mensajes si pulso en el buzon
  * click("a[id=btnCart]")
  * match html('title') contains 'Carrito'
  * driver.screenshot()

  # Proceso para eliminar una impresora
  * click("a[class=btn-perfil]")
  * click("a[id=openPrinter]")
  * waitFor('#test').click()
  * driver.screenshot()