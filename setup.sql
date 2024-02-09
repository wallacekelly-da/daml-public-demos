create or replace function pqs_watermark_trigger_func() returns trigger as $$
declare
  watermark_row json;
begin
  watermark_row := json_build_object(
    -- these are the columns on the table
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
