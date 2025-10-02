Jose Ulises Cortez Montoya 24/09/25 7:41 PM
Se creo Login y Pagina 
* Login utiliza usuario, contraseña y que se pueda ocultar la contraseña
* Pagina Inicial creada sin contenido

## Cambios realizados por Mariana Rodriguez Torres (Alias Marianeitor) 25/09/2025 11:37 PM 
Se creo la detección automática del banco y la marca de la tarjeta al escribir los primeros 4 dígitos de la de tarjeta.

### Reglas:
**5101** = Banco: **Nu**, Marca: **Mastercard**
**4152** = Banco: **BBVA**, Marca: **Visa**
**5579** = Banco: **Santander**, la marca queda vacía porque me dio flojera investigar cual era jijiji. 

### Detalles técnicos:
- Se agregó un `TextWatcher` en el campo **Número de tarjeta (firDigits)**.
- El campo **Banco (etBank)** se rellena automáticamente y no es editable manualmente.
- El campo **Marca (etBrand)** se rellena automáticamente cuando es de Nu y BBVA.
- Todo se mantiene dentro en la carp **AddCardActivity** si van a mover algo tengan cuidado con esos.

  ## Cambios realizados por Mariana Rodriguez Torres (Alias Marianeitor) 01/10/2025 9:50 PM 
Se agrego la vista calendario, donde se puede seleccionar fecha y agregar servicios que se deseen pagar en esa fecha. 


  
