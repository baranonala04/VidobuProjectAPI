package com.example.VidobuProje.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/test")
@Tag(name = "Test", description = "Test APIs for different access levels")
public class TestController {
    @Operation(summary = "Public access", description = "Get public content - no authentication required")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved public content",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string")))
    })
    @GetMapping("/all")
    public String allAccess() {
        return "Public Content.";
    }
    @Operation(summary = "User access", description = "Get user content - requires authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user content",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string")))
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/user")
    public String userAccess() {
        return "User Content.";
    }
}
