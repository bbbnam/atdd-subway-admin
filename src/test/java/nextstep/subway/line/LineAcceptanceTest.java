package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@SuppressWarnings("NonAsciiCharacters")
@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given, when
        ExtractableResponse<Response> 신분당선_응답 = LineRestHelper.지하철_라인_생성("bg-red-600", "신분당선");

        // then
        // 지하철_노선_생성됨
        assertThat(신분당선_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(신분당선_응답.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        String color = "bg-red-600";
        String lineName = "신분당선";
        LineRestHelper.지하철_라인_생성(color, lineName);

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> 라인_생성_응답 = LineRestHelper.지하철_라인_생성(color, lineName);

        // then
        // 지하철_노선_생성_실패됨
        assertThat(라인_생성_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }


    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> 신분당선_응답 = LineRestHelper.지하철_라인_생성("bg-red-600", "신분당선");

        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> 이호선_응답 = LineRestHelper.지하철_라인_생성("bg-green-600", "2호선");

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> 라인_전체_조회_응답 = LineRestHelper.지하철_라인_전체_조회();

        List<Long> expectedLineIds = Stream.of(신분당선_응답, 이호선_응답)
                .map(this::extractLocationByResponse)
                .collect(Collectors.toList());

        List<LineResponse> 라인_전체_조회_응답_객체 = 라인_전체_조회_응답.jsonPath().getList(".", LineResponse.class);
        List<Long> resultLineIds = 라인_전체_조회_응답_객체.stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());

        // then
        // 지하철_노선_목록_응답됨
        assertThat(라인_전체_조회_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
        // 지하철_노선_목록_포함됨
        assertThat(resultLineIds).containsAll(expectedLineIds);
        assertThat(라인_전체_조회_응답_객체.get(0).getName()).isEqualTo("신분당선");
        assertThat(라인_전체_조회_응답_객체.get(0).getColor()).isEqualTo("bg-red-600");
        assertThat(라인_전체_조회_응답_객체.get(1).getName()).isEqualTo("2호선");
        assertThat(라인_전체_조회_응답_객체.get(1).getColor()).isEqualTo("bg-green-600");
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> 신분당선_응답 = LineRestHelper.지하철_라인_생성("bg-red-600", "신분당선");

        // when
        // 지하철_노선_조회_요청
        Long lineId = extractLocationByResponse(신분당선_응답);
        ExtractableResponse<Response> 지하철_라인_조회_응답 = LineRestHelper.지하철_라인_조회(lineId);

        LineResponse 지하철_라인_조회_응답_객체 = 지하철_라인_조회_응답.body().as(LineResponse.class);

        // then
        // 지하철_노선_응답됨
        assertThat(지하철_라인_조회_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(지하철_라인_조회_응답_객체.getName()).isEqualTo("신분당선");
        assertThat(지하철_라인_조회_응답_객체.getColor()).isEqualTo("bg-red-600");
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> 신분당선_응답 = LineRestHelper.지하철_라인_생성("bg-red-600", "신분당선");

        // when
        // 지하철_노선_수정_요청
        Long lineId = extractLocationByResponse(신분당선_응답);
        Map<String, String> 수정될_신분당선 = LineRestHelper.lineParamsGenerator("bg-green-600", "2호선");

        ExtractableResponse<Response> 지하철_라인_수정_응답 = LineRestHelper.지하철_라인_수정(lineId, 수정될_신분당선);

        // then
        // 지하철_노선_수정됨
        assertThat(지하철_라인_수정_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> 신분당선_응답 = LineRestHelper.지하철_라인_생성("bg-red-600", "신분당선");

        // when
        // 지하철_노선_제거_요청
        Long lineId = extractLocationByResponse(신분당선_응답);
        ExtractableResponse<Response> 지하철_라인_삭제_응답 = LineRestHelper.지하철_라인_삭제(lineId);

        // then
        // 지하철_노선_삭제됨
        assertThat(지하철_라인_삭제_응답.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private Long extractLocationByResponse(ExtractableResponse<Response> response) {
        return Long.parseLong(response.header("Location").split("/")[2]);
    }
}
