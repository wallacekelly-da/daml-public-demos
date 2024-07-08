import java.nio.file.{Paths, Files}
import java.nio.charset.StandardCharsets

import com.digitalasset.canton.console.ParticipantReference

def enableOrGetParty(
    participant: ParticipantReference,
    partyId: String
): PartyId =
  participant.parties
    .list()
    .find(p =>
      p.party.uid.id == partyId && p.participants(0).participant == participant
    ) match {
    case Some(p) => p.party
    case None =>
      participant.parties.enable(partyId, waitForDomain = DomainChoice.All)
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
  val participantName = participant.uid.id
  val partyId = party.uid.toProtoPrimitive
  val partyName = party.uid.id
  writeId(participantName + "-" + partyName, partyId)
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
        isActive = true,
        annotations = Map(
          "description" -> description
        )
      )
  }

def writeParticipantId(participant: ParticipantReference) = {
  val participantId = participant.uid.toProtoPrimitive
  val participantName = participant.uid.id.toProtoPrimitive
  writeId(participantName, participantId)
}
