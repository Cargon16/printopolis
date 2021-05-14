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
  * driver.screenshot()
 
  # voy a mensajes si pulso en el buzon
  * click("a[id=btnCart]")
  * match html('title') contains 'Carrito'
  * driver.screenshot()

  # Añadir un evento de impresora
  * click("a[id=kaka]")
  * mouse('#calendar-day-36').click()
  * locateAll('.item')[0].click()
  * click("input[class=ok]")
  * click("a[id=goBack]")

  # Proceso para tramitar un pedido
  * click("a[id=boton]")
  * input('#fname', 'Fausto')
  * input('#email', 'fausto@gmail.com')
  * input('#adr', 'Calle Falsa 123')
  * input('#city', 'Faustinia')
  * input('#state', 'Faustilandia')
  * input('#zip', '27001')
  * input('#cname', 'Fausto Calamero')
  * input('#ccnum', '123458975621')
  * input('#expmonth', '06')
  * input('#expyear', '22')
  * input('#cvv', '159')
  * submit().click("input[id=checkout]")

  # Proceso para eliminar una impresora
  * click("a[class=btn-perfil]")
  * click("a[id=openPrinter]")
  * waitFor('#test').click()
  * driver.screenshot()

  # Subir un diseño
  * click("a[class=btn-perfil]")
  * click("button[id=boton_design]")
  * select('select[name=category]','Tecnologia')
  * input('#pre', '15')
  * input('#nom', 'PS5')
  * input('#dec', 'Carcasa PS5')
  * input('#vol', '20')
  * input('#vol', '20')
  * driver.inputFile('#modelo3D', './ps5.glb')
  * submit().click("input[id=subir]")
  * click("a[id=btnDesigns]")
  * delay(2000).screenshot()
