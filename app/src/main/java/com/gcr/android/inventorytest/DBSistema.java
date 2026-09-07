package com.gcr.android.inventorytest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import com.gcr.android.inventorytest.fcm.FireBaseNotificationManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import static com.gcr.android.inventorytest.Venta.VENTA_CONTADO;
import static com.gcr.android.inventorytest.Venta.VENTA_CREDITO;

public class DBSistema extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "miscelanea.db";
    private static final String MEDIDAS_TABLE_NAME = "MEDIDAS";
    private static final String MEDIDAS_TABLE_CREATE = "create table " + MEDIDAS_TABLE_NAME +
            " (id integer primary key autoincrement, " +
            "medida varchar(20) not null unique, " +
            "alias varchar(5) not null unique)";

    private static final String PRODUCTOS_TABLE_NAME = "productos";
    private static final String PRODUCTOS_TABLE_CREATE = "create table " + PRODUCTOS_TABLE_NAME +
            " (id integer primary key autoincrement, " +
            "barcode varchar(40) unique, " +
            "nombre varchar(30), " +
            "precio real," +
            "punto_reorden real," +
            "stock real, "+
            "idmedida integer not null, " +
            "foreign key(idmedida) references " + MEDIDAS_TABLE_NAME + "(id))";

    private static final String CLIENTES_TABLE_NAME = "clientes";
    private static final String CLIENTES_TABLE_CREATE = "create table " + CLIENTES_TABLE_NAME + " (" +
            "idcliente integer primary key autoincrement, " +
            "nombre varchar(50) not null, " +
            "apellidos varchar(60) not null, " +
            "telefono varchar(12) null, " +
            "correo varchar(60) unique null, " +
            "direccion varchar(200) null " +
            ")";

    private static final String VENTAS_TABLE_NAME = "ventas";
    private static final String VENTAS_TABLE_CREATE = "create table " + VENTAS_TABLE_NAME +
            " (id integer primary key autoincrement, " +
            "idcliente integer null default 0, " +
            "estatus integer not null default " + VENTA_CONTADO + ", " +
            "plazo date, " +
            "fecha date, " +
            "hora time, " +
            "total real, " +
            "pago real default 0, " +
            "foreign key (idcliente) references " + CLIENTES_TABLE_NAME + "(idcliente)" +
            ")";

    private static final String PRODUCTOS_VENTAS_TABLE_NAME = "productos_ventas";
    private static final String PRODUCTOS_VENTAS_TABLE_CREATE = "create table "+PRODUCTOS_VENTAS_TABLE_NAME+
            " (idproducto integer, "+
            "idventa integer, "+
            "cantidad double, "+
            "producto  varchar(30), "+
            "precioventa double, "+
            "foreign key (idproducto) references " + PRODUCTOS_TABLE_NAME + "(id), "+
            "foreign key (idventa) references " + VENTAS_TABLE_NAME + "(id), "+
            "primary key(idproducto, idventa))";

    private static final String LOTE_PRODUCTOS_TABLE_NAME="lote_productos";
    private  static final String LOTE_PRODUCTOS_TABLE_CREATE="create table "+LOTE_PRODUCTOS_TABLE_NAME + 
            " (id_lote integer primary key autoincrement, " +
            "numero real, " +
            "idproducto integer,"  +
            "cantidad real, "+
            "fentrada date, "+
            "fcaducidad date null, "+
            "foreign key (idproducto) references "+ PRODUCTOS_TABLE_NAME + "(id))";



    private static final String PAGOS_TABLE_NAME = "pagos";
    private static final String PAGOS_TABLE_CREATE = "create table " + PAGOS_TABLE_NAME +  " (" +
            "idpago integer primary key autoincrement, " +
            "idventa integer not null, " +
            "pago real not null default 0, " +
            "foreign key (idventa) references " + VENTAS_TABLE_NAME + "(id)" +
            ")";

    private static final String VISTA_CREATE_PROLOTE = "create view prolote as SELECT " +
            "id_lote, fcaducidad, nombre, numero FROM "+ LOTE_PRODUCTOS_TABLE_NAME +" inner join "
            + PRODUCTOS_TABLE_NAME + " on idproducto = id";

    private static final String VISTA_VENTAS_CLIENTES_NAME = "ventasclientes";
    private static final String VISTA_CREATE_VENTAS_CLIENTES = "create view "+ VISTA_VENTAS_CLIENTES_NAME +
            " as SELECT v.id, v.fecha, v.hora, v.plazo, v.total, v.pago, c.idcliente, c.nombre, c.apellidos" +
            " from " + VENTAS_TABLE_NAME + " v inner join " + CLIENTES_TABLE_NAME +
            " c on v.idcliente = c.idcliente where v.estatus = " + VENTA_CREDITO + " order by v.fecha, v.hora";


    public DBSistema(Context context){//SE CREA LA BASE DE DATOS
        super(context, DATABASE_NAME, null, 1);
        this.context = context;
        //SQLiteDatabase db = getWritableDatabase();
        //db.execSQL("drop view " + VISTA_VENTAS_CLIENTES_NAME);
        //db.execSQL(VISTA_CREATE_VENTAS_CLIENTES);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    @Deprecated
    public void setUpDatabase(Context context){
        File dbFile = context.getDatabasePath(DATABASE_NAME);//SE ABRE L
        if (!dbFile.exists())
            createTables();
    }

    public void setUpDatabase(){
        File dbFile = context.getDatabasePath(DATABASE_NAME);//SE ABRE L
        if (!dbFile.exists())
            createTables();
    }
//SE CREAN LAS TABLAS DE LA BASE DE DATOS
    private void createTables(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(MEDIDAS_TABLE_CREATE);
        db.execSQL(PRODUCTOS_TABLE_CREATE);
        db.execSQL(CLIENTES_TABLE_CREATE);//
        db.execSQL(VENTAS_TABLE_CREATE);
        db.execSQL(PRODUCTOS_VENTAS_TABLE_CREATE);
        db.execSQL(LOTE_PRODUCTOS_TABLE_CREATE);
        db.execSQL(PAGOS_TABLE_CREATE);//
        db.execSQL(VISTA_CREATE_PROLOTE);
        db.execSQL(VISTA_CREATE_VENTAS_CLIENTES);
        db.execSQL("insert into " + MEDIDAS_TABLE_NAME + " (medida, alias)" +
                " values ('Unidad', 'Uni'), ('Kilogramos', 'Kg')");
        db.close();
    }
//INSERTAR LOS PRODUCTOS EN LA BASE DE DATOS
    public long insertProducto(String nombre, String barcode, double precio, double reorden, double stock, int idmedida){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre",nombre);
        values.put("barcode",barcode);
        values.put("precio", precio);
        values.put("punto_reorden", reorden);
        values.put("stock", stock);
        values.put("idmedida", idmedida);
        long result = db.insert(PRODUCTOS_TABLE_NAME, null, values);
        db.close();
        return result;
    }

    //insertar productos al lote

    public  long InsertarLote(int numero,double cantidad,String caducidad, String entrada,int idproducto){

        SQLiteDatabase db=getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put("numero",numero);
        values.put("idproducto",idproducto);
        values.put("cantidad",cantidad);
        values.put("fentrada",entrada);
        values.put("fcaducidad", caducidad);


        long resultado = db.insert(LOTE_PRODUCTOS_TABLE_NAME, null, values);
        db.close();
        return resultado;
    }

    public ArrayList<Lote> SelectLote(int id){

        ArrayList<Lote> lote= new ArrayList<>();
        SQLiteDatabase db=getReadableDatabase();

        Cursor cursor=db.rawQuery("select * from "+LOTE_PRODUCTOS_TABLE_NAME+" where idproducto="+id,null);
        while(cursor.moveToNext()){
            Lote aux= new Lote(cursor.getInt(cursor.getColumnIndex("id_lote"))
            ,cursor.getInt(cursor.getColumnIndex("numero"))
            ,cursor.getString(cursor.getColumnIndex("fcaducidad"))
            ,cursor.getString(cursor.getColumnIndex("fentrada"))
            ,cursor.getInt(cursor.getColumnIndex("cantidad")));
            lote.add(aux);
        }
        cursor.close();
        db.close();
        return lote;
    }

    public Double SelectStock(int id){
        Double stock = 0.0;
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("select stock from " + PRODUCTOS_TABLE_NAME + " where id = " + id, null);

        if(cursor.moveToNext()){
            stock = cursor.getDouble(cursor.getColumnIndex("stock"));
        }
        db.close();
        cursor.close();
        return stock;
    }

    public int SelectNumeroLote(int id){
         int numero=0;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor=db.rawQuery("select numero from " + LOTE_PRODUCTOS_TABLE_NAME + " where idproducto = " + id + " order by numero desc limit 1", null);

        if(cursor.getColumnCount() == 0){
            numero = 0;
        }else{
            if (cursor.moveToNext()){
                numero = cursor.getInt(cursor.getColumnIndex("numero"));
            }
        }
        cursor.close();
        return numero;
    }
public ArrayList<Lote> Select_Lote_Caducidad(String caducidad) {
    SQLiteDatabase db = getReadableDatabase();
    ArrayList<Lote> lote = new ArrayList<>();
    Cursor cursor = db.rawQuery("select * from " + LOTE_PRODUCTOS_TABLE_NAME + " where fcaducidad=" + caducidad, null);
while (cursor.moveToNext()) {
    Lote aux = new Lote(cursor.getInt(cursor.getColumnIndex("id_lote"))
            , cursor.getInt(cursor.getColumnIndex("numero"))
            , cursor.getString(cursor.getColumnIndex("fcaducidad"))
            , cursor.getString(cursor.getColumnIndex("fentrada"))
            , cursor.getInt(cursor.getColumnIndex("cantidad")));

    aux.setId_producto(cursor.getInt(cursor.getColumnIndex("idproducto")));
    lote.add(aux);
}
    cursor.close();
    db.close();
return lote;
}


//METODO PARA ACTUALIZAR LOS PRODUCTOS
    public int updateProducto(int id, String nombre, String barcode, double precio, double reorden, double stock, int idmedida){
        SQLiteDatabase db = getWritableDatabase();//MODO ESCRITURA
        ContentValues values = new ContentValues();
        values.put("nombre",nombre);
        values.put("barcode",barcode);
        values.put("precio",precio);
        values.put("punto_reorden",reorden);
        values.put("stock", stock);
        values.put("idmedida",idmedida);
        int result = db.update(PRODUCTOS_TABLE_NAME, values, "id = " + id, null); //ACTUALIZAR LOS PRODUCTOS SI REGRESA UN -1 LA OPERACION NO SE REALIZAN
        db.close();//CIERRA LA BASE DE DATOS
        return result;//REGRESA EL RESULTADO
    }

    public int updatestock(int id, double stock){

        SQLiteDatabase db=getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put("stock",stock);
        int resultado=db.update(PRODUCTOS_TABLE_NAME, values, "id=" + id, null);
        db.close();
        return resultado;
    }
//ELIMINAR LOS PRODUCTOS
    public int deleteProducto(int id){
        SQLiteDatabase db = getWritableDatabase();
        int result = db.delete(PRODUCTOS_TABLE_NAME, "id=" + id, null);
        db.close();
        return result;
    }
//SELECCIONA UN PRODUCTO EN ESPECIFICO
    public Producto selectProducto(int id){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + PRODUCTOS_TABLE_NAME + " where id=" + id, null);
        Producto producto=null;
        while (cursor.moveToNext()){
            producto= new Producto(cursor.getString(cursor.getColumnIndex("nombre")),
                    cursor.getString(cursor.getColumnIndex("barcode")),
                    cursor.getInt(cursor.getColumnIndex("id")),
                    cursor.getDouble(cursor.getColumnIndex("precio")));

            producto.setReorden(cursor.getDouble(cursor.getColumnIndex("punto_reorden")));
            producto.setStock(cursor.getDouble(cursor.getColumnIndex("stock")));
            Medida medida = selectMedida(cursor.getInt(cursor.getColumnIndex("idmedida")));
            producto.setMedida(medida);
        }
        cursor.close();
        db.close();
        return producto;
    }

    public Producto selectProducto(String barcode){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + PRODUCTOS_TABLE_NAME + " where barcode='" + barcode + "'", null);
        Producto producto = null;
        while (cursor.moveToNext()){
            producto = new Producto(cursor.getString(cursor.getColumnIndex("nombre")),
                    cursor.getString(cursor.getColumnIndex("barcode")),
                    cursor.getInt(cursor.getColumnIndex("id")),
                    cursor.getDouble(cursor.getColumnIndex("precio")));
            producto.setReorden(cursor.getDouble(cursor.getColumnIndex("punto_reorden")));
            producto.setStock(cursor.getDouble(cursor.getColumnIndex("stock")));
        }
        cursor.close();
        db.close();
        return producto;
    }

    //Muestra la lista de clientes
    public ArrayList<Cliente> selectClientes() {

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + CLIENTES_TABLE_NAME, null);
        ArrayList<Cliente> clientes = new ArrayList<>();

        while (cursor.moveToNext()) {
            Cliente aux = new Cliente(cursor.getInt(cursor.getColumnIndex("idcliente")),
                    cursor.getString(cursor.getColumnIndex("nombre")),
                    cursor.getString(cursor.getColumnIndex("apellidos")));
            aux.setTelefono(cursor.getString(cursor.getColumnIndex("telefono")));
            aux.setCorreo(cursor.getString(cursor.getColumnIndex("correo")));
            aux.setDireccion(cursor.getString(cursor.getColumnIndex("direccion")));
            clientes.add(aux);
        }
        cursor.close();
        db.close();
        return clientes;
    }

//MUESTRA LOS PRODUCTOS
    public ArrayList<Producto> selectProductos(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from "+PRODUCTOS_TABLE_NAME, null);
        ArrayList<Producto> productos = new ArrayList<>();
        while (cursor.moveToNext()){
            Producto aux = new Producto(cursor.getString(cursor.getColumnIndex("nombre")),
                    cursor.getString(cursor.getColumnIndex("barcode")),
                    cursor.getInt(cursor.getColumnIndex("id")),
                    cursor.getDouble(cursor.getColumnIndex("precio")));

            aux.setReorden(cursor.getDouble(cursor.getColumnIndex("punto_reorden")));
            aux.setStock(cursor.getDouble(cursor.getColumnIndex("stock")));
            Medida medida = selectMedida(cursor.getInt(cursor.getColumnIndex("idmedida")));
            aux.setMedida(medida);
            productos.add(aux);
        }
        cursor.close();
        db.close();
        return productos;
    }

    public long insertCliente(Cliente cliente){

        long resultado;

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre", cliente.getNombre());
        values.put("apellidos", cliente.getApellidos());
        values.put("telefono", cliente.getTelefono());
        values.put("correo", cliente.getCorreo());
        values.put("direccion", cliente.getDireccion());

        resultado = db.insert(CLIENTES_TABLE_NAME, null, values);

        return resultado;
    }

    public void insertProductoVenta(ArrayList<Producto> productos, String[] fecha, double total,
                                    double pago, int tipo_venta, Cliente cliente, String plazo) {
        SQLiteDatabase db = getWritableDatabase();
        int id_cliente = 0;
        //preguntar el tipo de venta, si es a contado agregar el cliente a la base da datos
        if (tipo_venta == VENTA_CREDITO) {
            if (cliente.id == 0) {
                long r = insertCliente(cliente);
                if (r != -1) {
                    Cursor cursor = db.rawQuery("SELECT last_insert_rowid() from " + CLIENTES_TABLE_NAME, null);
                    cursor.moveToFirst();
                    id_cliente = cursor.getInt(0);
                    cursor.close();
                }
            } else {
                id_cliente = cliente.id;
            }
        }

        //Insertar una venta nueva
        ContentValues values = new ContentValues();
        values.put("fecha", fecha[0]);
        values.put("idcliente", id_cliente);
        values.put("estatus", tipo_venta);
        values.put("plazo", plazo);
        values.put("hora", fecha[1]);
        values.put("total", total);
        values.put("pago", pago);
        db.insert(VENTAS_TABLE_NAME, null, values);
        //Fin de insertar venta

        //Recuperar el ultimo id de la venta nueva insertada
        Cursor cursor = db.rawQuery("SELECT last_insert_rowid()", null);
        cursor.moveToFirst();
        int lastID = cursor.getInt(0);
        cursor.close();

        //Insertar en la tabla de ventas_productos
        db.beginTransaction();
        SQLiteStatement statement = db.compileStatement("insert into " + PRODUCTOS_VENTAS_TABLE_NAME + " values (?, ?, ?, ?, ?)");
        for (Producto p : productos) {
            statement.clearBindings();
            statement.bindLong(1, p.getId());
            statement.bindLong(2, lastID);
            statement.bindDouble(3, p.getCantidad());
            statement.bindString(4, p.getNombre());
            statement.bindDouble(5, p.getPrecio());
            statement.executeInsert();
        }
        db.setTransactionSuccessful();
        db.endTransaction();

        //Actualizar la existentencia de los productos del inventario
        for (Producto p : productos) {
            values = new ContentValues();
            double cantidad = p.getCantidad();
            double stock = p.getStock();
            double nstock = stock - cantidad;
            values.put("stock", nstock);
            db.update(PRODUCTOS_TABLE_NAME, values, "id = " + p.getId(), null);
            if(nstock <= p.getReorden()){
                FireBaseNotificationManager.sendAndroidNotificacion(p.getNombre(), nstock, context, ListadoProductosActivity.class);
                FireBaseNotificationManager.sendFcmNotification(p.getNombre(), nstock, context);
            }
        }
        db.close();
    }

    public ArrayList<Venta> selectVentas(){
        ArrayList<Venta> ventas= new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + VENTAS_TABLE_NAME, null);
        while (cursor.moveToNext()){
            ventas.add(new Venta(cursor.getInt(cursor.getColumnIndex("id")),
                    cursor.getString(cursor.getColumnIndex("fecha")),
                    cursor.getString(cursor.getColumnIndex("hora")),
                    cursor.getDouble(cursor.getColumnIndex("total")),
                    cursor.getDouble(cursor.getColumnIndex("pago"))));
        }
        cursor.close();
        db.close();
        return ventas;
    }

    //Activity estadisticas
    public ArrayList<Venta> selectVentasContado(){
        ArrayList<Venta> ventas= new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + VENTAS_TABLE_NAME + " where estatus = " + VENTA_CONTADO, null);
        while (cursor.moveToNext()){
            ventas.add(new Venta(cursor.getInt(cursor.getColumnIndex("id")),
                    cursor.getString(cursor.getColumnIndex("fecha")),
                    cursor.getString(cursor.getColumnIndex("hora")),
                    cursor.getDouble(cursor.getColumnIndex("total")),
                    cursor.getDouble(cursor.getColumnIndex("pago"))));
        }
        cursor.close();
        db.close();
        return ventas;
    }


    public ArrayList<HashMap<String, Object>> selectDetalleVenta(int id){
        ArrayList<HashMap<String, Object>> detalle = new ArrayList<>();
        HashMap<String, Object> aux;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select pv.producto, pv.precioventa, pv.cantidad, v.fecha, v.hora, v.total, v.pago from "+
                PRODUCTOS_VENTAS_TABLE_NAME+" pv join "+VENTAS_TABLE_NAME+" v on pv.idventa=v.id where v.id="+id, null);
        while (cursor.moveToNext()){
            aux = new HashMap<>();
            aux.put("nombre", cursor.getString(cursor.getColumnIndex("producto")));
            aux.put("precio", cursor.getDouble(cursor.getColumnIndex("precioventa")));
            aux.put("cantidad", cursor.getDouble(cursor.getColumnIndex("cantidad")));
            aux.put("fecha", cursor.getString(cursor.getColumnIndex("fecha")));
            aux.put("hora", cursor.getString(cursor.getColumnIndex("hora")));
            aux.put("total", cursor.getDouble(cursor.getColumnIndex("total")));
            aux.put("pago", cursor.getDouble(cursor.getColumnIndex("pago")));
            detalle.add(aux);
        }
        cursor.close();
        db.close();
        return  detalle;
    }

    public ArrayList<ProductosVendidos> selectVendidos() {

        ArrayList<ProductosVendidos> productosvendidos= new ArrayList<>();

        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor=db.rawQuery("select pv.producto,pv.cantidad,v.fecha from "
                + PRODUCTOS_VENTAS_TABLE_NAME + " pv join " + VENTAS_TABLE_NAME
                +" v on pv.idventa=v.id", null);

        while (cursor.moveToNext()){
            productosvendidos.add(new ProductosVendidos(cursor.getString(cursor.getColumnIndex("producto")),
                    cursor.getInt(cursor.getColumnIndex("cantidad")),
                    cursor.getString(cursor.getColumnIndex("fecha"))));
        }

        cursor.close();
        db.close();

        return productosvendidos;
    }

    /*
    public ArrayList<HashMap<String, Object>> selectDetalleVenta(int id){
        ArrayList<HashMap<String, Object>> detalle = new ArrayList<>();
        HashMap<String, Object> aux;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select p.nombre, pv.precioventa, pv.cantidad, v.fecha, v.hora, v.total, v.pago from ("+
                PRODUCTOS_TABLE_NAME+" p join "+PRODUCTOS_VENTAS_TABLE_NAME+" pv on p.id=pv.idproducto) join "+VENTAS_TABLE_NAME+
                " v on pv.idventa=v.id where v.id="+id, null);
        while (cursor.moveToNext()){
            aux = new HashMap<>();
            aux.put("nombre", cursor.getString(cursor.getColumnIndex("nombre")));
            aux.put("precio", cursor.getDouble(cursor.getColumnIndex("precioventa")));
            aux.put("cantidad", cursor.getDouble(cursor.getColumnIndex("cantidad")));
            aux.put("fecha", cursor.getString(cursor.getColumnIndex("fecha")));
            aux.put("hora", cursor.getString(cursor.getColumnIndex("hora")));
            aux.put("total", cursor.getDouble(cursor.getColumnIndex("total")));
            aux.put("pago", cursor.getDouble(cursor.getColumnIndex("pago")));
            detalle.add(aux);
        }

        cursor.close();
        db.close();
        return  detalle;
    }
     */


    public float valor(){

        Stack valor = new Stack();
        float total = 0;
        String sql = "select precio, stock from " + PRODUCTOS_TABLE_NAME;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor=db.rawQuery(sql, null);
        while(cursor.moveToNext()){
            valor.add(cursor.getFloat(cursor.getColumnIndex("precio")) * cursor.getFloat(cursor.getColumnIndex("stock")));
        }

        for(int i = 0; i < valor.size(); i++){
             total = (float) valor.get(i) + total;
        }
        db.close();
        cursor.close();
        return total;
    }

    //Fechas
    public ArrayList<Prolote> prolot(){

        ArrayList<Prolote> prol=new ArrayList<>();
        SQLiteDatabase db= getReadableDatabase();
        Cursor cursor= db.rawQuery("select * from prolote where fcaducidad !=''", null);
        int res;
        while (cursor.moveToNext()){
            res=dias_res(cursor.getString(cursor.getColumnIndex("fcaducidad")));

            prol.add(new Prolote(cursor.getInt(cursor.getColumnIndex("id_lote")),cursor.getString(cursor.getColumnIndex("nombre")),
                    cursor.getInt(cursor.getColumnIndex("numero")),res));

        }

cursor.close();
        db.close();
        return prol;
    }

    public int dias_res(String fecha){
        int res = 0;
        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor= db.rawQuery("select julianday('" + fecha + "'" + ") - (julianday('NOW') - 1) as res", null);
        if(cursor.moveToNext()){
            res=cursor.getInt(cursor.getColumnIndex("res"));
        }
        db.close();
        cursor.close();
        return res;
    }

    //Ventas por Clientes
    public ArrayList<VentaCliente> ventas_por_cliente() {
        ArrayList<VentaCliente> VC = new ArrayList<>();
        SQLiteDatabase db= getReadableDatabase();
        Cursor cursor= db.rawQuery("select * from " + VISTA_VENTAS_CLIENTES_NAME, null);

        while (cursor.moveToNext()) {
            Cliente cliente = new Cliente(cursor.getInt(cursor.getColumnIndex("idcliente")),
                    cursor.getString(cursor.getColumnIndex("nombre")),
                    cursor.getString(cursor.getColumnIndex("apellidos")));

            VentaCliente aux = new VentaCliente(cursor.getInt(cursor.getColumnIndex("id")),
                    cursor.getString(cursor.getColumnIndex("fecha")),
                    cursor.getString(cursor.getColumnIndex("hora")),
                    cursor.getDouble(cursor.getColumnIndex("total")),
                    cursor.getDouble(cursor.getColumnIndex("pago")), cliente);

            VC.add(aux);
        }
        cursor.close();
        db.close();
        return VC;
    }

    //Activity venta a credito
    public Venta selectVentaCredito(int idventa) {
        Venta venta = null;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor= db.rawQuery("SELECT * FROM " + VENTAS_TABLE_NAME + " WHERE ID = " + idventa +
                " AND ESTATUS = " + VENTA_CREDITO, null);

        while (cursor.moveToNext()) {
            venta = new Venta(cursor.getInt(cursor.getColumnIndex("ID")),
                    cursor.getString(cursor.getColumnIndex("FECHA")),
                    cursor.getString(cursor.getColumnIndex("HORA")),
                    cursor.getDouble(cursor.getColumnIndex("TOTAL")),
                    cursor.getDouble(cursor.getColumnIndex("PAGO")));
            venta.setPlazo(cursor.getString(cursor.getColumnIndex("PLAZO")));
            venta.setEstatus(cursor.getInt(cursor.getColumnIndex("ESTATUS")));
        }

        cursor.close();
        db.close();

        return venta;
    }

    //Activity venta a credito actualizar el pago de una venta a credito
    public boolean updateVentapago(int id, double pago) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("pago", pago);

        int result = db.update(VENTAS_TABLE_NAME, values, "ID = " + id, null);
        db.close();
        return result != -1;
    }

    //Activity venta a credito actualizar la venta a venta a Contado
    public boolean updateVentaCredito(int id, Venta venta) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("pago", venta.getPago());
        values.put("estatus", venta.getEstatus());

        int result = db.update(VENTAS_TABLE_NAME, values, "ID = " + id, null);
        db.close();
        return result != -1;
    }


    public Cliente selectCliente(int id){
        Cliente cliente = null;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + CLIENTES_TABLE_NAME + " where idcliente = " + id, null);

        while (cursor.moveToNext()) {
            cliente = new Cliente(cursor.getInt(cursor.getColumnIndex("idcliente")),
                    cursor.getString(cursor.getColumnIndex("nombre")),
                    cursor.getString(cursor.getColumnIndex("apellidos")));
            cliente.setTelefono(cursor.getString(cursor.getColumnIndex("telefono")));
            cliente.setCorreo(cursor.getString(cursor.getColumnIndex("correo")));
            cliente.setDireccion(cursor.getString(cursor.getColumnIndex("direccion")));
        }
        cursor.close();
        db.close();

        return cliente;
    }

    public int updateCliente(int id, Cliente cliente) {
        int result;
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("nombre", cliente.getNombre());
        values.put("apellidos", cliente.getApellidos());
        values.put("telefono", cliente.getTelefono());
        values.put("correo", cliente.getCorreo());
        values.put("direccion", cliente.getDireccion());

        try {//se lanza una excepcion al intentar guardar un correo electronico ya existente
            result = db.update(CLIENTES_TABLE_NAME, values, "idcliente = " + id, null);
        } catch (Exception e) {
            result = -1;
        }
        db.close();
        return result;
    }

    public long insertMedida(Medida medida) {
        long result;
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("medida", medida.toString());
        values.put("alias", medida.getAlias());

        result = db.insert(MEDIDAS_TABLE_NAME, null, values);

        return result;
    }

    public Medida selectMedida(int id) {
        //create table medidas (id integer primary key autoincrement, medida varchar(20) not null)
        Medida medida = null;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + MEDIDAS_TABLE_NAME + " where id = " + id, null);

        while (cursor.moveToNext()) {
            medida = new Medida(cursor.getInt(cursor.getColumnIndex("id")),
                    cursor.getString(cursor.getColumnIndex("medida")),
                    cursor.getString(cursor.getColumnIndex("alias")));
        }
        db.close();
        cursor.close();
        return medida;
    }

    public ArrayList<Medida> selectMedidas() {
        ArrayList<Medida> medidas = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + MEDIDAS_TABLE_NAME + " order by medida", null);

        while (cursor.moveToNext()){
            Medida medida = new Medida(cursor.getInt(cursor.getColumnIndex("id")),
                    cursor.getString(cursor.getColumnIndex("medida")),
                    cursor.getString(cursor.getColumnIndex("alias")));
            medidas.add(medida);
        }
        cursor.close();
        db.close();
        return medidas;
    }

    public ArrayList<ProductosVendidos> selectVendidos(int idventa){

        ArrayList<ProductosVendidos> productosvendidos = new ArrayList<>();

        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor=db.rawQuery("select pv.producto, pv.cantidad, pv.precioventa, v.fecha from "
                + PRODUCTOS_VENTAS_TABLE_NAME + " pv join " + VENTAS_TABLE_NAME
                + " v on pv.idventa = v.id where v.id = " + idventa, null);

        while (cursor.moveToNext()){

            productosvendidos.add(new ProductosVendidos(cursor.getString(cursor.getColumnIndex("producto")),
                    cursor.getInt(cursor.getColumnIndex("cantidad")),
                    cursor.getString(cursor.getColumnIndex("fecha")),
                    cursor.getDouble(cursor.getColumnIndex("precioventa"))));

        }
        cursor.close();
        db.close();
        return productosvendidos;
    }
}