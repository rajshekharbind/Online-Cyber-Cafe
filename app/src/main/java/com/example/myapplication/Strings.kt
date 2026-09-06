package com.example.myapplication

/**
 * Req 53 — Centralized Localization Wrapper
 *
 * All user-visible strings are defined here so the app can be easily
 * translated without hunting through every composable file.
 *
 * Usage: AppStrings.Auth.loginTitle
 */
object AppStrings {

    /** Authentication screens */
    object Auth {
        const val loginTitle       = "Welcome Back"
        const val loginSubtitle    = "Sign in to continue"
        const val registerTitle    = "Create Account"
        const val registerSubtitle = "Join thousands of successful applicants"
        const val emailLabel       = "Email Address"
        const val passwordLabel    = "Password"
        const val loginButton      = "Sign In"
        const val registerButton   = "Create Account"
        const val forgotPassword   = "Forgot Password?"
        const val noAccount        = "Don't have an account? Register"
        const val hasAccount       = "Already have an account? Sign In"
    }

    /** Student dashboard */
    object Student {
        const val dashboardTitle        = "My Dashboard"
        const val applyNow              = "Apply Now"
        const val myApplications        = "My Applications"
        const val trackApplication      = "Track Application"
        const val documentVault         = "Document Vault"
        const val profileIncomplete     = "Complete your profile to apply"
        const val noApplicationsFound   = "No applications found"
        const val viewDetails           = "View Details →"
    }

    /** Job listing */
    object Jobs {
        const val browseJobs            = "Browse Government Jobs"
        const val noJobsAvailable       = "No jobs available right now. Check back soon!"
        const val applicationFee        = "Application Fee"
        const val serviceFee            = "Service Fee"
        const val totalAmount           = "Total Payable"
        const val deadline              = "Last Date"
        const val applyForJob           = "Apply for this Job"
        const val alreadyApplied        = "Already Applied"
    }

    /** Payment screens */
    object Payment {
        const val checkoutTitle         = "Secure Checkout"
        const val paymentSummary        = "Payment Summary"
        const val selectMethod          = "Select Payment Method"
        const val processingPayment     = "Securing your payment…"
        const val payButton             = "Pay Safely"
        const val sslNote               = "SSL Encrypted & Secure"
        const val receiptTitle          = "Payment Receipt"
        const val downloadReceipt       = "Download Receipt"
        const val trustSection          = "Trust & Transparency"
        const val humanSupport          = "Human Support"
    }

    /** Application flow */
    object ApplicationFlow {
        const val summaryTitle          = "Application Summary"
        const val confirmProfile        = "I confirm my Profile Information is accurate."
        const val confirmDocs           = "I have uploaded all Required Documents in Vault."
        const val confirmDetails        = "I have verified the Application Details & Deadlines."
        const val confirmTerms          = "I agree to the Service Terms & Conditions."
        const val confirmConsent        = "I authorize the Cyber Cafe to process my application."
        const val proceedToPayment      = "Proceed to Payment"
        const val confirmAllItems       = "Confirm all items to continue"
        const val duplicateTitle        = "Application Already Submitted"
        const val duplicateBody         = "You have already applied for this job. Submitting again is not allowed."
    }

    /** Support screens */
    object Support {
        const val helpSupportTitle      = "Help & Support"
        const val raiseTicket           = "Raise a Support Ticket"
        const val paymentFailed         = "Payment Failed"
        const val trackingHelp          = "Job Application Status"
        const val humanFirstSection     = "Human-First Support"
        const val talkHumanChat         = "Talk to a Human (Live Chat)"
        const val talkHumanCall         = "Talk to a Human (Call Us)"
    }

    /** Employee section */
    object Employee {
        const val dashboardTitle        = "Employee Console"
        const val todayOverview         = "Today's Overview"
        const val yourAssignments       = "Your Assignments"
        const val priorityFirst         = "Priority First"
        const val workspaceTitle        = "Application Workspace"
        const val startProcessing       = "Start Processing"
        const val markSubmitted         = "Mark Submitted"
        const val portalUnavailable     = "Portal Unavailable"
        const val requestVerification   = "Req Verification"
        const val readinessChecklist    = "Application Readiness Checklist"
        const val checkDocs             = "All required documents uploaded"
        const val checkPayment          = "Payment status verified"
        const val checkDeadline         = "Deadline is acceptable"
    }

    /** Admin section */
    object Admin {
        const val dashboardTitle        = "Admin Dashboard"
        const val applicationDashboard  = "Application Dashboard"
        const val criticalAlert         = "URGENT: Critical Applications Approaching Deadline"
        const val totalUsers            = "Total Users"
        const val newUsers              = "New Users (Today)"
        const val totalRevenue          = "Total Revenue"
        const val pendingApplications   = "Pending"
        const val addManualJob          = "Add Job Manually"
        const val saveJob               = "Save Job"
    }

    /** Generic errors (Req 52) */
    object Errors {
        const val networkError          = "No internet connection. Please check your network settings."
        const val serverBusy            = "Our servers are busy right now. Please try again in a few minutes."
        const val unauthorised          = "Incorrect email or password. Please try again."
        const val notFound              = "The requested information was not found. Please refresh."
        const val duplicate             = "A duplicate entry already exists. Please check your details."
        const val generic               = "Something went wrong. Please try again or contact support."
    }
}
