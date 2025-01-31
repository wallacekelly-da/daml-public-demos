select(has("active_contracts"))
  .active_contracts[]
  .interface_views[]
  .view_value
  .fields 
  | {
      owner: (map(select(.label == "owner")) | .[0].value.text),
      id: (map(select(.label == "id")) | .[0].value.text),
      quantity: (map(select(.label == "quantity")) | .[0].value.int64),
      version: (map(select(.label == "version")) | .[0].value.text)
    }