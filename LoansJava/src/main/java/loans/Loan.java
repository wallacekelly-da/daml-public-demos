package loans;

import com.daml.ledger.javaapi.data.ContractFilter;
import com.daml.ledger.javaapi.data.CreateAndExerciseCommand;
import com.daml.ledger.javaapi.data.CreateCommand;
import com.daml.ledger.javaapi.data.CreatedEvent;
import com.daml.ledger.javaapi.data.DamlRecord;
import com.daml.ledger.javaapi.data.ExerciseCommand;
import com.daml.ledger.javaapi.data.Identifier;
import com.daml.ledger.javaapi.data.Party;
import com.daml.ledger.javaapi.data.Template;
import com.daml.ledger.javaapi.data.Text;
import com.daml.ledger.javaapi.data.Timestamp;
import com.daml.ledger.javaapi.data.Unit;
import com.daml.ledger.javaapi.data.Value;
import com.daml.ledger.javaapi.data.codegen.Choice;
import com.daml.ledger.javaapi.data.codegen.ContractCompanion;
import com.daml.ledger.javaapi.data.codegen.ContractTypeCompanion;
import com.daml.ledger.javaapi.data.codegen.Created;
import com.daml.ledger.javaapi.data.codegen.Exercised;
import com.daml.ledger.javaapi.data.codegen.PrimitiveValueDecoders;
import com.daml.ledger.javaapi.data.codegen.Update;
import com.daml.ledger.javaapi.data.codegen.ValueDecoder;
import da.internal.template.Archive;
import java.lang.Deprecated;
import java.lang.IllegalArgumentException;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public final class Loan extends Template {
  public static final Identifier TEMPLATE_ID = new Identifier(
      "519ade95609bc50ce16a00de0166f6423baf6d778081671cdaba984ee4eea5ab", "Loans", "Loan");

  public static final Choice<Loan, Archive, Unit> CHOICE_Archive = Choice.create("Archive", value$ -> value$.toValue(),
      value$ -> Archive.valueDecoder()
          .decode(value$),
      value$ -> PrimitiveValueDecoders.fromUnit.decode(value$));

  public static final ContractCompanion.WithoutKey<Contract, ContractId, Loan> COMPANION = new ContractCompanion.WithoutKey<>(
      "loans.Loan", TEMPLATE_ID, ContractId::new,
      v -> Loan.templateValueDecoder().decode(v), Contract::new, List.of(CHOICE_Archive));

  public final String lender;

  public final String borrower;

  public final String amount;

  public final String rate;

  public final Instant created;

  public Loan(String lender, String borrower, String amount, String rate, Instant created) {
    this.lender = lender;
    this.borrower = borrower;
    this.amount = amount;
    this.rate = rate;
    this.created = created;
  }

  @Override
  public Update<Created<com.daml.ledger.javaapi.data.codegen.ContractId<Loan>>> create() {
    return new Update.CreateUpdate<com.daml.ledger.javaapi.data.codegen.ContractId<Loan>, Created<com.daml.ledger.javaapi.data.codegen.ContractId<Loan>>>(
        new CreateCommand(Loan.TEMPLATE_ID, this.toValue()), x -> x, ContractId::new);
  }

  /**
   * @deprecated since Daml 2.3.0; use {@code createAnd().exerciseArchive} instead
   */
  @Deprecated
  public Update<Exercised<Unit>> createAndExerciseArchive(Archive arg) {
    return createAnd().exerciseArchive(arg);
  }

  public static Update<Created<com.daml.ledger.javaapi.data.codegen.ContractId<Loan>>> create(
      String lender, String borrower, String amount, String rate, Instant created) {
    return new Loan(lender, borrower, amount, rate, created).create();
  }

  @Override
  public CreateAnd createAnd() {
    return new CreateAnd(this);
  }

  @Override
  protected ContractCompanion.WithoutKey<Contract, ContractId, Loan> getCompanion() {
    return COMPANION;
  }

  /**
   * @deprecated since Daml 2.5.0; use {@code valueDecoder} instead
   */
  @Deprecated
  public static Loan fromValue(Value value$) throws IllegalArgumentException {
    return valueDecoder().decode(value$);
  }

  public static ValueDecoder<Loan> valueDecoder() throws IllegalArgumentException {
    return ContractCompanion.valueDecoder(COMPANION);
  }

  public DamlRecord toValue() {
    ArrayList<DamlRecord.Field> fields = new ArrayList<DamlRecord.Field>(5);
    fields.add(new DamlRecord.Field("lender", new Party(this.lender)));
    fields.add(new DamlRecord.Field("borrower", new Party(this.borrower)));
    fields.add(new DamlRecord.Field("amount", new Text(this.amount)));
    fields.add(new DamlRecord.Field("rate", new Text(this.rate)));
    fields.add(new DamlRecord.Field("created", Timestamp.fromInstant(this.created)));
    return new DamlRecord(fields);
  }

  private static ValueDecoder<Loan> templateValueDecoder() throws IllegalArgumentException {
    return value$ -> {
      Value recordValue$ = value$;
      List<DamlRecord.Field> fields$ = PrimitiveValueDecoders.recordCheck(5, recordValue$);
      String lender = PrimitiveValueDecoders.fromParty.decode(fields$.get(0).getValue());
      String borrower = PrimitiveValueDecoders.fromParty.decode(fields$.get(1).getValue());
      String amount = PrimitiveValueDecoders.fromText.decode(fields$.get(2).getValue());
      String rate = PrimitiveValueDecoders.fromText.decode(fields$.get(3).getValue());
      Instant created = PrimitiveValueDecoders.fromTimestamp.decode(fields$.get(4).getValue());
      return new Loan(lender, borrower, amount, rate, created);
    };
  }

  public static ContractFilter<Contract> contractFilter() {
    return ContractFilter.of(COMPANION);
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (object == null) {
      return false;
    }
    if (!(object instanceof Loan)) {
      return false;
    }
    Loan other = (Loan) object;
    return Objects.equals(this.lender, other.lender) &&
        Objects.equals(this.borrower, other.borrower) &&
        Objects.equals(this.amount, other.amount) && Objects.equals(this.rate, other.rate) &&
        Objects.equals(this.created, other.created);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.lender, this.borrower, this.amount, this.rate, this.created);
  }

  @Override
  public String toString() {
    return String.format("loans.Loan(%s, %s, %s, %s, %s)", this.lender, this.borrower, this.amount,
        this.rate, this.created);
  }

  public static final class ContractId extends com.daml.ledger.javaapi.data.codegen.ContractId<Loan>
      implements Exercises<ExerciseCommand> {
    public ContractId(String contractId) {
      super(contractId);
    }

    @Override
    protected ContractTypeCompanion<? extends com.daml.ledger.javaapi.data.codegen.Contract<ContractId, ?>, ContractId, Loan, ?> getCompanion() {
      return COMPANION;
    }

    public static ContractId fromContractId(
        com.daml.ledger.javaapi.data.codegen.ContractId<Loan> contractId) {
      return COMPANION.toContractId(contractId);
    }
  }

  public static class Contract extends com.daml.ledger.javaapi.data.codegen.Contract<ContractId, Loan> {
    public Contract(ContractId id, Loan data, Optional<String> agreementText,
        Set<String> signatories, Set<String> observers) {
      super(id, data, agreementText, signatories, observers);
    }

    @Override
    protected ContractCompanion<Contract, ContractId, Loan> getCompanion() {
      return COMPANION;
    }

    public static Contract fromIdAndRecord(String contractId, DamlRecord record$,
        Optional<String> agreementText, Set<String> signatories, Set<String> observers) {
      return COMPANION.fromIdAndRecord(contractId, record$, agreementText, signatories, observers);
    }

    public static Contract fromCreatedEvent(CreatedEvent event) {
      return COMPANION.fromCreatedEvent(event);
    }
  }

  public interface Exercises<Cmd> extends com.daml.ledger.javaapi.data.codegen.Exercises<Cmd> {
    default Update<Exercised<Unit>> exerciseArchive(Archive arg) {
      return makeExerciseCmd(CHOICE_Archive, arg);
    }
  }

  public static final class CreateAnd extends com.daml.ledger.javaapi.data.codegen.CreateAnd
      implements Exercises<CreateAndExerciseCommand> {
    CreateAnd(Template createArguments) {
      super(createArguments);
    }

    @Override
    protected ContractTypeCompanion<? extends com.daml.ledger.javaapi.data.codegen.Contract<ContractId, ?>, ContractId, Loan, ?> getCompanion() {
      return COMPANION;
    }
  }
}
