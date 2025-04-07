# Driver Dashboard and Process Flow

This directory contains all the screens and components related to the driver role in the FuelTrackerQR application.

## Driver Process Flow

The driver process flow consists of the following steps:

1. **Request Fuel**: Driver creates a new fuel request specifying the amount, vehicle, and trip details.
2. **Pending Approval**: The request is sent to an admin for approval.
3. **Get QR Code**: Once approved, the driver can view and download a QR code for the request.
4. **Fuel Dispensed**: The driver presents the QR code at a gas station to receive fuel.
5. **View History**: The driver can track fuel usage history and statistics.

## Screens

### DriverDashboardScreen
The main dashboard for drivers showing:
- Process flow visualization
- Current fuel requests grouped by status
- Assigned vehicles
- Quick actions

### DriverProfileScreen
Displays the driver's profile information:
- Personal information
- Assigned vehicles
- Fuel usage statistics

### NewRequestScreen
Form for creating a new fuel request:
- Vehicle selection
- Fuel amount
- Trip details
- Notes

### FuelHistoryScreen
Shows the history of fuel requests:
- Monthly breakdown
- Total usage statistics
- Detailed request history

### VehicleDetailsScreen
Detailed information about a specific vehicle:
- Vehicle specifications
- Fuel usage for this vehicle
- Fuel level visualization

### QRCodeDisplayScreen
Displays the QR code for an approved fuel request:
- QR code visualization
- Request details
- Sharing options

### RequestDetailsScreen
Shows detailed information about a specific request:
- Request status and details
- Approval information
- Dispensing details (if dispensed)

## Components

### DriverProcessFlow
A visual representation of the driver process flow with the current step highlighted.

### DriverProcessDiagram
A diagram showing the relationships between different states in the fuel request process.

## Visual Enhancements

The driver screens feature:
- Gradient backgrounds and accents
- Status-specific color coding
- Animated transitions between sections
- Expandable/collapsible sections
- Visual process flow indicators
- Card-based UI with shadows and rounded corners
- Consistent iconography using custom icons

## Custom Icons

Custom icons used in the driver screens:
- DirectionsCar: For vehicle-related UI
- LocalGasStation: For fuel-related UI
- History: For history-related UI
- PendingActions: For pending requests
- QrCodeScanner: For QR code scanning
