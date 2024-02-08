-- create or replace function pqs_contract_trigger_func() returns trigger as $$
-- declare
--   contract_row json;
-- begin
--   contract_row := json_build_object(
--     'table', TG_TABLE_NAME,
--     'action', TG_OP,
--     'pk', NEW.pk,
--     'contract_key', NEW.contract_key,
--     'payload', NEW.payload
--   );
--   perform pg_notify('pqs_contract_changes', contract_row::text);
--   return new;
-- end;
-- $$ language plpgsql;

create or replace function pqs_watermark_trigger_func() returns trigger as $$
declare
  watermark_row json;
begin
  watermark_row := json_build_object(
    'offset', NEW.offset,
    'ix', NEW.ix
  );
  perform pg_notify('pqs_watermark_changes', watermark_row::text);
  return new;
end;
$$ language plpgsql;

create or replace trigger pqs_watermark_trigger
  after insert or update or delete on _watermark
  for each row execute function pqs_watermark_trigger_func();
