create table ims135693_nt (c0 number, c1 number, c2 number, c3 number, c4 number);
create table ims135693_t (c0 number, c1 number, c2 number, c3 number, c4 number);
create table ims100168 (c_num_pk number primary key, c_num number, c_var varchar(100), c_clob clob);
create table ims147068 (c1 number primary key, c2 clob, c3 nclob, c4 blob);

create table ims184347 (c1 number(20,10));
create table ims184465 (code varchar2(100) primary key,filename varchar2(100),blobdata blob);
CREATE OR REPLACE DIRECTORY load_DIR AS '/home/prosync/dml_case/issue/ims184465';
CREATE OR REPLACE PROCEDURE table_load_image (
   p_dir        IN   VARCHAR2
 , p_id         IN   VARCHAR2
 , p_file_name  IN   VARCHAR2
 , p_photo_name IN   VARCHAR2
)
IS
   l_source   BFILE;
   l_dest     BLOB;
   l_length   BINARY_INTEGER;
BEGIN
   l_source := BFILENAME (p_dir, p_photo_name);

   INSERT INTO ims184465
       (code,filename,blobdata)
   VALUES
       (p_id, p_file_name, EMPTY_BLOB())
   RETURNING blobdata
        INTO l_dest;
   -- lock record
  SELECT blobdata
    INTO l_dest
    FROM ims184465
   WHERE code       = p_id
     FOR UPDATE;
   -- open the file
   DBMS_LOB.fileopen (l_source, DBMS_LOB.file_readonly);
   -- get length
   l_length := DBMS_LOB.getlength (l_source);
   -- read the file and store in the destination
   DBMS_LOB.loadfromfile (l_dest, l_source, l_length);
   -- update the blob field with destination
   UPDATE ims184465
      SET blobdata  = l_dest
    WHERE code      = p_id;
   -- close file
   DBMS_LOB.fileclose (l_source);
   commit;
END;
/
