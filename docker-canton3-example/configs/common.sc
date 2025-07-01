import java.nio.file.{Paths, Files}
import java.nio.charset.StandardCharsets

import com.digitalasset.canton.console.ParticipantReference

def enableOrGetParty(
    participant: ParticipantReference,
    partyName: String
): PartyId =
  participant.parties
    .list()
    .find(p =>
      p.party.uid.identifier.str == partyName && p.party.uid.namespace == participant.namespace
    ) match {
    case Some(p) => p.party
    case None =>
      participant.parties.enable(partyName)
  }

def createOrGetUser(
    participant: ParticipantReference,
    userId: String,
    partyId: PartyId,
    isAdmin: Boolean,
    description: String
): User =
  participant.ledger_api.users
    .list()
    .users
    .filter(u => u.id == userId)
    .headOption match {
    case Some(u) => u
    case None =>
      participant.ledger_api.users.create(
        id = userId,
        actAs = Set(partyId),
        readAs = Set(partyId),
        primaryParty = Some(partyId),
        participantAdmin = isAdmin,
        identityProviderAdmin = isAdmin,
        annotations = Map(
          "description" -> description
        )
      )
  }

def writeId(name: String, id: String) = {
  Files.write(
    Paths.get("/canton/host/configs/" + name + ".id"),
    id.getBytes(StandardCharsets.UTF_8)
  )
  Files.write(
    Paths.get("/canton/host/configs/" + name + ".json"),
    ("\"" + id + "\"").getBytes(StandardCharsets.UTF_8)
  )
}

def writePartyId(participant: ParticipantReference, party: PartyId) = {
  val participantName = participant.uid.identifier.toString()
  val partyName = party.uid.identifier.toString()
  val partyId = party.toProtoPrimitive
  writeId(participantName + "-" + partyName, partyId)
}

def writeParticipantId(participant: ParticipantReference) = {
  val participantName = participant.uid.identifier.toString()
  val participantId = participant.toProtoPrimitive
  writeId(participantName, participantId)
}
