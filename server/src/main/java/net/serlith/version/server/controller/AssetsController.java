package net.serlith.version.server.controller;

import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.util.unit.DataSize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@NullMarked
@RestController
@RequestMapping("/api/v1/assets")
public class AssetsController {

    @Value("classpath:logo.png")
    private Resource logo;

    private final int bufferSize = Math.toIntExact(DataSize.ofKilobytes(4).toBytes());

    @GetMapping(value = "/logo.png", produces = MediaType.IMAGE_PNG_VALUE)
    public Flux<DataBuffer> fetchLogo() {
        return DataBufferUtils.read(
                this.logo,
                new DefaultDataBufferFactory(),
                this.bufferSize
        );
    }

}
