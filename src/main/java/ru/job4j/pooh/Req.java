package ru.job4j.pooh;

public class Req {

    private final String httpRequestType;
    private final String poohMode;
    private final String sourceName;
    private final String param;

    public Req(String httpRequestType, String poohMode, String sourceName, String param) {
        this.httpRequestType = httpRequestType;
        this.poohMode = poohMode;
        this.sourceName = sourceName;
        this.param = param;
    }

    public static Req of(String content) {
        String[] params = parse(content);
        for (String str : params) {
            System.out.println(str);
        }

        return new Req(params[0], params[1], params[2], params[3]);
    }

    public String httpRequestType() {
        return httpRequestType;
    }

    public String getPoohMode() {
        return poohMode;
    }

    public String getSourceName() {
        return sourceName;
    }

    public String getParam() {
        return param;
    }

    private static String[] parse(String content) {
        String[] splitContent = content.split(System.lineSeparator());
        if (splitContent.length < 2) {
            throw new IllegalArgumentException("Incorrect input parameters.");
        }
        String[] typeModeSource = splitContent[0].split(" ");

        if (typeModeSource.length != 3) {
            throw new IllegalArgumentException("Incorrect input parameters.");
        }
        String httpRequestType = typeModeSource[0];

        String param;
        String poohMode;
        String sourceName;

        if (!"POST".equals(httpRequestType) && !"GET".equals(httpRequestType)) {
            throw new IllegalArgumentException("Incorrect input parameters.");
        } else if ("POST".equals(httpRequestType)) {
            String[] modeSource = typeModeSource[1].split("/");
            if (modeSource.length != 3) {
                throw new IllegalArgumentException("Incorrect mode or source parameters.");
            }
            poohMode = modeSource[1];
            sourceName = modeSource[2];
            param = splitContent[splitContent.length - 1];
        } else {
            String[] modeSource = typeModeSource[1].split("/");
            if (modeSource.length != 3 && modeSource.length != 4) {
                throw new IllegalArgumentException("Incorrect mode or source parameters.");
            } else if (modeSource.length == 3) {
                poohMode = modeSource[1];
                sourceName = modeSource[2];
                param = "";
            } else {
                poohMode = modeSource[1];
                sourceName = modeSource[2];
                param = modeSource[3];
            }
        }
        return new String[]{httpRequestType, poohMode, sourceName, param};
    }

}
