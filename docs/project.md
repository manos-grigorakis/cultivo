# Cultivo — Project Specification

## Overview

Cultivo is a plant lifecycle tracker, that allows users to track their plants by logging their actions into events to create the plant lifecycle history.

## Problem & Goal

**Target Users:** Myself

**Problem:** Lack of organized plant records, without keeping logs of performed actions.

**Goal:** Users should be able to keep track of the lifecycle of their plants by recording event logs in the application to create the plant history.

## Core Workflow

1. Create a new plant
2. Log events such as watering, notes, harvest.
3. View event history of each plant

## MVP Scope

**Status:** FROZEN

The initial release will support:

- Plant Management
  - View plants (excluding archived)
  - Add a new plant
  - View plant details
  - Edit plant information
  - Archive plant
  - View archived plants
- Plant Event Management
  - Record plant events
  - View plant event history
  - Edit existing events

## Out of Scope

The following are not part of the current MVP:

- Seeds inventory
- Sowing lifecycle
- Care task notification for the plant
- AI assistant for plants
- Authentication & Authorization

## Future Ideas

Potential improvements for later releases (not MVP commitments):

- Care task notification for the plant
- Information about the plant (such as care tips)
- Analytics

## MVP Complete When

The MVP is considered complete when:

- [ ] User can create a plant and log events about the plant to track its lifecycle
- [ ] A plant that was created has ACTIVE status by default
- [ ] User can edit existing plants and events
- [ ] User can archive a plant regardless of its current status
- [ ] An archived plant is not being displayed in the main plant view
- [ ] User can view event history of plants
- [ ] An event cannot exist without an associated plant

## Domain Overview

### Core Concepts

- **Plant:** A plant represents a real plant that the user monitors
- **Event:** An event represents a performed action or observation recorded for a plant

### Relationships

- A plant can have multiple events
- An event belongs only to one plant

## Domain Rules & Constraints

- A plant can exist without any events
- A plant can be ACTIVE or DEAD
- Default status of a plant is ACTIVE
- Allowed plant status transition is from ACTIVE to DEAD
- A plant can be archived regardless of its status
- Archived plants are excluded from the default plant list
- Archived plants remain accessible for viewing
- Archived plants cannot be modified or receive new events
- An event cannot exist without an associated plant
