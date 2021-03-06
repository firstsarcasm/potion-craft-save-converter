package org.firstsarcasm.potion.craft.save.converter.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.firstsarcasm.potion.craft.save.converter.dto.PlayerPrefsDto;
import org.firstsarcasm.potion.craft.save.converter.dto.PlayerStateDto;
import org.firstsarcasm.potion.craft.save.converter.dto.SaveFileDto;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

public class PcSavesConverter {

	private static final String SAVE_FILES_DELIMITER = "\n";
	private static final byte[] KEY = "a^19uh%47x71e%sd".getBytes(StandardCharsets.UTF_8);

	private final ObjectMapper objectMapper = new ObjectMapper();

	public String fromJson(List<String> jsonStrings) {
		return encode(jsonStrings.get(0)) + "\n" + encode(jsonStrings.get(1));
	}

	@SneakyThrows
	public String fromJsonObject(SaveFileDto saveFileDto) {
		PlayerStateDto playerStateDto = saveFileDto.getPlayerStateDto();
		PlayerPrefsDto playerPrefsDto = saveFileDto.getPlayerPrefsDto();
		String state = objectMapper.writeValueAsString(playerStateDto);
		String prefs = objectMapper.writeValueAsString(playerPrefsDto);
		return encode(state) + SAVE_FILES_DELIMITER + encode(prefs);
	}

	@SneakyThrows
	public SaveFileDto toJsonObject(List<String> saveStrings) {
		byte[] stateBase64 = Base64.getDecoder().decode(saveStrings.get(0));
		byte[] playerPrefsBase64 = Base64.getDecoder().decode(saveStrings.get(1));
		String state = new String(encode(stateBase64));
		String playerPrefs = new String(encode(playerPrefsBase64));

		PlayerStateDto playerStateDto = objectMapper.readValue(state, PlayerStateDto.class);
		PlayerPrefsDto playerPrefsDto = objectMapper.readValue(playerPrefs, PlayerPrefsDto.class);
		return new SaveFileDto(playerStateDto, playerPrefsDto);
	}

	public String toJson(List<String> saveStrings) {
		byte[] stateBase64 = Base64.getDecoder().decode(saveStrings.get(0));
		byte[] playerPrefsBase64 = Base64.getDecoder().decode(saveStrings.get(1));
		String state = new String(encode(stateBase64));
		String playerPrefs = new String(encode(playerPrefsBase64));
		return state + SAVE_FILES_DELIMITER + playerPrefs;
	}

	private byte[] encode(byte[] dataBytes) {
		int num = 0;
		for (int i = 0; i < dataBytes.length; i++) {
			dataBytes[i] ^= KEY[num];
			if (++num == KEY.length) {
				num = 0;
			}
		}
		return dataBytes;
	}

	private String encode(String dataString) {
		byte[] bytes = dataString.getBytes(StandardCharsets.UTF_8);
		byte[] encodedBytes = encode(bytes);
		byte[] base64 = Base64.getEncoder().encode(encodedBytes);
		return new String(base64);
	}
}
