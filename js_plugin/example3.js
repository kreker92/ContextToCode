ydn.db.crud.req.WebSql.parseRow = function(row, store) {

  if (store.isFixed() && !store.usedInlineKey() && store.countIndex() == 0 &&
      row[ydn.db.base.DEFAULT_BLOB_COLUMN]) {
    // check for blob or file
    var s = row[ydn.db.base.DEFAULT_BLOB_COLUMN];
    var BASE64_MARKER = ';base64,';
  //  if (s.indexOf(BASE64_MARKER) == -1) {
  //    return ydn.json.parse(s);
  //  } 
  }
  