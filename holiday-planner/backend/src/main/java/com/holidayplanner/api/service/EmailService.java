package com.holidayplanner.api.service;

import com.holidayplanner.api.model.HolidayPlan;
import com.holidayplanner.api.model.DailyPlan;
import com.holidayplanner.api.model.Activity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@Service
public class EmailService {
    
    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.username:holiday-planner@example.com}")
    private String fromEmail;
    
    @Value("${app.name:Holiday Planner AI}")
    private String appName;
    
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    
    /**
     * Send welcome email when holiday plan is created
     */
    @Async
    public void sendHolidayPlanCreatedEmail(HolidayPlan holidayPlan) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(holidayPlan.getUserEmail());
            helper.setSubject("🎉 Your Holiday Plan is Ready! - " + holidayPlan.getDestination());
            
            String htmlContent = generateHolidayPlanCreatedHtml(holidayPlan);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
        } catch (MessagingException e) {
            // In production, use proper logging framework
            System.err.println("Failed to send holiday plan created email: " + e.getMessage());
        }
    }
    
    /**
     * Send daily weather update email
     */
    @Async
    public void sendWeatherUpdateEmail(HolidayPlan holidayPlan) {
        try {
            // Find today's plan
            DailyPlan todaysPlan = holidayPlan.getDailyPlans().stream()
                    .filter(dp -> dp.getDate().equals(LocalDate.now()))
                    .findFirst()
                    .orElse(null);
            
            if (todaysPlan == null) {
                return; // No plan for today
            }
            
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(holidayPlan.getUserEmail());
            helper.setSubject("🌤️ Daily Weather Update - " + holidayPlan.getDestination());
            
            String htmlContent = generateDailyWeatherUpdateHtml(holidayPlan, todaysPlan);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
        } catch (MessagingException e) {
            System.err.println("Failed to send weather update email: " + e.getMessage());
        }
    }
    
    /**
     * Send holiday plan cancellation email
     */
    @Async
    public void sendHolidayPlanCancelledEmail(HolidayPlan holidayPlan) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(holidayPlan.getUserEmail());
            message.setSubject("Holiday Plan Cancelled - " + holidayPlan.getDestination());
            message.setText(generateCancellationText(holidayPlan));
            
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send cancellation email: " + e.getMessage());
        }
    }
    
    /**
     * Send daily reminder email for upcoming activities
     */
    @Async
    public void sendDailyReminderEmail(HolidayPlan holidayPlan, DailyPlan dailyPlan) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(holidayPlan.getUserEmail());
            helper.setSubject("🌅 Today's Adventure Awaits! - " + holidayPlan.getDestination());
            
            String htmlContent = generateDailyReminderHtml(holidayPlan, dailyPlan);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
        } catch (MessagingException e) {
            System.err.println("Failed to send daily reminder email: " + e.getMessage());
        }
    }
    
    /**
     * Generate HTML content for holiday plan created email
     */
    private String generateHolidayPlanCreatedHtml(HolidayPlan holidayPlan) {
        StringBuilder html = new StringBuilder();
        
        html.append("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                    .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                    .highlight { background: #e3f2fd; padding: 15px; border-radius: 5px; margin: 15px 0; }
                    .activity { background: white; padding: 15px; margin: 10px 0; border-radius: 5px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
                    .footer { text-align: center; margin-top: 30px; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🎉 Your Holiday Plan is Ready!</h1>
                        <h2>""").append(holidayPlan.getDestination()).append("""
            </h2>
                    </div>
                    <div class="content">
                        <div class="highlight">
                            <h3>📅 Trip Details</h3>
                            <p><strong>Destination:</strong> """).append(holidayPlan.getDestination()).append("""
            </p>
                            <p><strong>Start Date:</strong> """).append(holidayPlan.getStartDate().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"))).append("""
            </p>
                            <p><strong>Duration:</strong> """).append(holidayPlan.getNumberOfDays()).append("""
             days</p>
                            <p><strong>Activity Focus:</strong> """).append(holidayPlan.getActivityCategory()).append("""
            </p>
                        </div>
                        
                        <h3>🤖 AI-Powered Planning</h3>
                        <p>Our intelligent system has analyzed weather patterns and created a personalized itinerary just for you! Each day is optimized based on weather conditions to ensure you have the best possible experience.</p>
                        
                        <h3>📱 What's Next?</h3>
                        <ul>
                            <li>Check your daily plans in the app</li>
                            <li>Receive weather updates and activity adjustments</li>
                            <li>Get daily reminders and recommendations</li>
                        </ul>
                        
                        <div class="highlight">
                            <h4>🌤️ Smart Weather Integration</h4>
                            <p>We'll continuously monitor weather changes and send you daily updates with optimized activity suggestions. Indoor activities are automatically scheduled for rainy or extremely hot days, while outdoor adventures are planned for perfect weather!</p>
                        </div>
                    </div>
                    <div class="footer">
                        <p>Powered by """).append(appName).append("""
             - Your AI Travel Companion</p>
                    </div>
                </div>
            </body>
            </html>
            """);
        
        return html.toString();
    }
    
    /**
     * Generate HTML content for daily weather update email
     */
    private String generateDailyWeatherUpdateHtml(HolidayPlan holidayPlan, DailyPlan dailyPlan) {
        StringBuilder html = new StringBuilder();
        
        html.append("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #74b9ff 0%, #0984e3 100%); color: white; padding: 20px; text-align: center; border-radius: 10px 10px 0 0; }
                    .content { background: #f9f9f9; padding: 20px; border-radius: 0 0 10px 10px; }
                    .weather-card { background: white; padding: 20px; border-radius: 10px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); margin: 20px 0; }
                    .activity { background: #e8f5e8; padding: 15px; margin: 10px 0; border-radius: 5px; border-left: 4px solid #4caf50; }
                    .precautions { background: #fff3cd; padding: 15px; border-radius: 5px; border-left: 4px solid #ffc107; }
                    .recommendations { background: #d1ecf1; padding: 15px; border-radius: 5px; border-left: 4px solid #17a2b8; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🌤️ Weather Update</h1>
                        <h2>""").append(holidayPlan.getDestination()).append(" - ").append(LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"))).append("""
            </h2>
                    </div>
                    <div class="content">
                        <div class="weather-card">
                            <h3>Today's Weather</h3>
                            <p><strong>Condition:</strong> """).append(dailyPlan.getWeatherCondition()).append("""
            </p>
                            <p><strong>Temperature:</strong> """).append(String.format("%.1f°C", dailyPlan.getTemperature())).append("""
            </p>
                            <p><strong>Humidity:</strong> """).append(dailyPlan.getHumidity()).append("""
            %</p>
                            <p><strong>Wind Speed:</strong> """).append(String.format("%.1f km/h", dailyPlan.getWindSpeed())).append("""
            </p>
                        </div>
                        
                        <div class="recommendations">
                            <h3>📝 AI Recommendations</h3>
                            <p>""").append(dailyPlan.getGeneralRecommendations()).append("""
            </p>
                        </div>
                        
                        """);
        
        if (dailyPlan.getPrecautions() != null && !dailyPlan.getPrecautions().isEmpty()) {
            html.append("""
                <div class="precautions">
                    <h3>⚠️ Important Precautions</h3>
                    <div>""").append(dailyPlan.getPrecautions().replace("\n", "<br>")).append("""
                </div>
                </div>
                """);
        }
        
        html.append("""
                        <h3>🎯 Today's Activities</h3>
                        """);
        
        for (Activity activity : dailyPlan.getActivities()) {
            html.append("""
                <div class="activity">
                    <h4>""").append(activity.getTimeOfDay().getDescription()).append(" - ").append(activity.getName()).append("""
            </h4>
                    <p>""").append(activity.getDescription()).append("""
            </p>
                    <p><strong>Time:</strong> """).append(activity.getStartTime()).append(" - ").append(activity.getEndTime()).append("""
            </p>
                    <p><strong>Location:</strong> """).append(activity.getLocation()).append("""
            </p>
                    """);
            
            if (activity.getEquipmentNeeded() != null) {
                html.append("<p><strong>Bring:</strong> ").append(activity.getEquipmentNeeded()).append("</p>");
            }
            
            html.append("</div>");
        }
        
        html.append("""
                    </div>
                    <div style="text-align: center; margin-top: 30px; color: #666; font-size: 12px;">
                        <p>Stay tuned for tomorrow's weather update!</p>
                        <p>Powered by """).append(appName).append("""
             - Your AI Travel Companion</p>
                    </div>
                </div>
            </body>
            </html>
            """);
        
        return html.toString();
    }
    
    /**
     * Generate HTML content for daily reminder email
     */
    private String generateDailyReminderHtml(HolidayPlan holidayPlan, DailyPlan dailyPlan) {
        StringBuilder html = new StringBuilder();
        
        html.append("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #fd79a8 0%, #e84393 100%); color: white; padding: 20px; text-align: center; border-radius: 10px 10px 0 0; }
                    .content { background: #f9f9f9; padding: 20px; border-radius: 0 0 10px 10px; }
                    .activity-schedule { background: white; padding: 15px; margin: 10px 0; border-radius: 5px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
                    .morning { border-left: 4px solid #ffeaa7; }
                    .afternoon { border-left: 4px solid #fab1a0; }
                    .evening { border-left: 4px solid #6c5ce7; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🌅 Today's Adventure Awaits!</h1>
                        <h2>""").append(holidayPlan.getDestination()).append("""
            </h2>
                    </div>
                    <div class="content">
                        <h3>🗓️ Your Schedule for Today</h3>
                        """);
        
        for (Activity activity : dailyPlan.getActivities()) {
            String timeClass = switch (activity.getTimeOfDay()) {
                case MORNING -> "morning";
                case AFTERNOON -> "afternoon";
                case EVENING -> "evening";
                default -> "";
            };
            
            html.append("""
                <div class="activity-schedule """).append(timeClass).append("""
            ">
                    <h4>""").append(activity.getTimeOfDay().getDescription()).append(" - ").append(activity.getName()).append("""
            </h4>
                    <p><strong>⏰ Time:</strong> """).append(activity.getStartTime()).append(" - ").append(activity.getEndTime()).append("""
            </p>
                    <p><strong>📍 Location:</strong> """).append(activity.getLocation()).append("""
            </p>
                    <p>""").append(activity.getDescription()).append("""
            </p>
                </div>
                """);
        }
        
        html.append("""
                        <div style="background: #dff0d8; padding: 15px; border-radius: 5px; margin-top: 20px;">
                            <h4>💡 Today's Weather Tips</h4>
                            <p>""").append(dailyPlan.getWeatherSummary()).append("""
            </p>
                        </div>
                        
                        <div style="text-align: center; margin-top: 20px;">
                            <p><strong>Have an amazing day exploring """).append(holidayPlan.getDestination()).append("""
            !</strong></p>
                        </div>
                    </div>
                    <div style="text-align: center; margin-top: 30px; color: #666; font-size: 12px;">
                        <p>Powered by """).append(appName).append("""
             - Your AI Travel Companion</p>
                    </div>
                </div>
            </body>
            </html>
            """);
        
        return html.toString();
    }
    
    /**
     * Generate cancellation email text
     */
    private String generateCancellationText(HolidayPlan holidayPlan) {
        return String.format("""
            Dear Traveler,
            
            Your holiday plan for %s (starting %s) has been cancelled as requested.
            
            If you change your mind, you can always create a new plan through our app!
            
            We hope to help you plan another amazing adventure soon.
            
            Best regards,
            %s Team
            """, 
            holidayPlan.getDestination(),
            holidayPlan.getStartDate().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")),
            appName
        );
    }
}