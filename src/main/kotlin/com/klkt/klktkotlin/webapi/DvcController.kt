package com.klkt.klktkotlin.webapi


import com.fasterxml.jackson.databind.JsonNode
import com.klkt.klktkotlin.repo.DvcRepo
import com.klkt.klktkotlin.repo.DvcTestRepo
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import lombok.extern.slf4j.Slf4j
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.WebApplicationContext

@Slf4j
@RestController
@RequestMapping(value = ["/v1/" + "dvc"])
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
@Api(tags = ["Dịch vụ công dev"])
class DvcController(private val dvcRepo: DvcRepo, private val dvcTestRepo: DvcTestRepo) {
    private val logger: Logger = LoggerFactory.getLogger(this.javaClass as Class<*>)

    @PostMapping("get")
    @ApiOperation(value = "Display greeting message to non-admin user", response = JsonNode::class)
    @ApiResponses(
        value = arrayOf(
            ApiResponse(code = 200, message = "OK"),
            ApiResponse(code = 404, message = "The resource not found")
        )
    )
    fun getData(@RequestBody objInput: JsonNode): ResponseEntity<JsonNode?> {
       this.logger.info("objInput Dev: {}", objInput)
        return ResponseEntity<JsonNode?>(
            this.dvcRepo.getData().toJsonNode(),
            HttpStatus.OK
        )
    }

    @PostMapping("get-test")
    @ApiOperation(value = "Display greeting message to non-admin user", response = JsonNode::class)
    @ApiResponses(
        value = *arrayOf(
            ApiResponse(code = 200, message = "OK"),
            ApiResponse(code = 404, message = "The resource not found")
        )
    )
    fun getDataTest(@RequestBody objInput: JsonNode): ResponseEntity<JsonNode?>? {
        this.logger.info("objInput Test: {}", objInput)
        return ResponseEntity.ok(this.dvcTestRepo.getData().toJsonNode())
    }
}